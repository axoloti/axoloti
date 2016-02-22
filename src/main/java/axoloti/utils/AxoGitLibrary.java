package axoloti.utils;

import axoloti.Axoloti;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.CheckoutCommand;
import org.eclipse.jgit.api.CheckoutResult;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.CreateBranchCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.StatusCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.NoWorkTreeException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.submodule.SubmoduleWalk;
import org.eclipse.jgit.transport.PushResult;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

public class AxoGitLibrary extends AxolotiLibrary {

    public static String TYPE = "git";

    public AxoGitLibrary(String id, String type, String lloc, boolean e, String rloc, boolean auto) {
        super(id, type, lloc, e, rloc, auto);

    }

    public AxoGitLibrary() {
    }

    @Override
    public void reportStatus() {
        File f = new File(getLocalLocation());
        if (!f.exists()) {
            Logger.getLogger(AxoGitLibrary.class.getName()).log(Level.WARNING, "Status : {0} : local directory missing ", logDetails());
        }

        // get repository
        Git git;
        try {
            Repository repository;
            if (usingSubmodule()) {
                // special case, in developer mode, we have the repos as sub modules, these need to be accessed via the parent repo
                String relDir = System.getProperty(Axoloti.RELEASE_DIR);
                Git parent = Git.open(new File(relDir));
                File ldir = new File(getLocalLocation());
                String ldirstr = ldir.getName();
                repository = SubmoduleWalk.getSubmoduleRepository(parent.getRepository(), ldirstr);
                if (repository == null) {
                    Logger.getLogger(AxoGitLibrary.class.getName()).log(Level.WARNING, "status FAILED cannot find submodule : {0}", logDetails());
                    return;
                }
            } else {
                repository = Git.open(new File(getLocalLocation())).getRepository();
            }
            git = new Git(repository);
            reportStatus(git);
            git.getRepository().close();

        } catch (IOException ex) {
            Logger.getLogger(AxoGitLibrary.class.getName()).log(Level.SEVERE, null, ex);
            Logger.getLogger(AxoGitLibrary.class.getName()).log(Level.WARNING, "Status: exception {0}", logDetails());
        }
    }

    @Override
    public void sync() {

        // get repository
        Git git = null;
        try {
            Repository repository;
            if (usingSubmodule()) {
                // special case, in developer mode, we have the repos as sub modules, these need to be accessed via the parent repo
                String relDir = System.getProperty(Axoloti.RELEASE_DIR);
                Git parent = Git.open(new File(relDir));
                File ldir = new File(getLocalLocation());
                String ldirstr = ldir.getName();
                repository = SubmoduleWalk.getSubmoduleRepository(parent.getRepository(), ldirstr);
                if (repository == null) {
                    Logger.getLogger(AxoGitLibrary.class.getName()).log(Level.WARNING, "sync repo FAILED cannot find submodule : {0}", logDetails());
                    return;
                }
            } else {
                repository = Git.open(new File(getLocalLocation())).getRepository();
            }
            git = new Git(repository);

        } catch (IOException ex) {
            Logger.getLogger(AxoGitLibrary.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (git != null) {
            if (!pull(git)) {
                git.getRepository().close();
                return;
            }
            boolean isDirty = isDirty(git);
            if (isDirty && isAuth()) {
                Logger.getLogger(AxoGitLibrary.class.getName()).log(Level.INFO, "Modifications detected : {0}", logDetails());
                if (!add(git)) {
                    git.getRepository().close();
                    return;
                }
                if (!commit(git)) {
                    git.getRepository().close();
                    return;
                }
                if (!push(git)) {
                    git.getRepository().close();
                    return;
                }
                Logger.getLogger(AxoGitLibrary.class.getName()).log(Level.INFO, "Modifications uploaded : {0}", logDetails());
                reportStatus(git);
            }
            if (!checkout(git)) {
                git.getRepository().close();
                return;
            }
            Logger.getLogger(AxoGitLibrary.class.getName()).log(Level.INFO, "Sync Successful : {0}", logDetails());
	    git.getRepository().close();
        }
    }

    @Override
    public void init(boolean delete) {
        File ldir = new File(getLocalLocation());

        if (!usingSubmodule()) {
            if (getRemoteLocation() == null || getRemoteLocation().length() == 0) {
                Logger.getLogger(AxoGitLibrary.class.getName()).log(Level.WARNING, "init FAILED - no remote specified : {0}", logDetails());
                return;
            }

            if (delete && ldir.exists()) {
                try {
                    delete(ldir);
                } catch (IOException ex) {
                    Logger.getLogger(AxoGitLibrary.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            if (!ldir.exists()) {
                ldir.mkdirs();
            }

            String branch = getBranch();
            CloneCommand cmd = Git.cloneRepository();
            cmd.setURI(getRemoteLocation());
            cmd.setDirectory(ldir);
            cmd.setBranch(branch);
            if (isAuth()) {
                cmd.setCredentialsProvider(new UsernamePasswordCredentialsProvider(getUserId(), getPassword()));
            }
            try {
                Git git=cmd.call();
		git.getRepository().close();
                Logger.getLogger(AxoGitLibrary.class.getName()).log(Level.INFO, "Repo initialised Successfully : {0}", logDetails());
            } catch (Exception ex) {
                Logger.getLogger(AxoGitLibrary.class.getName()).log(Level.WARNING, "init repo FAILED : {0}", getId());
                Logger.getLogger(AxoGitLibrary.class.getName()).log(Level.SEVERE, null, ex);
                return;
            }
        } else {
            Logger.getLogger(AxoGitLibrary.class.getName()).log(Level.INFO, "Developer mode do NOT clone repo : {0}", logDetails());
        }
        // sync afterwards to ensure on correct branch
        sync();
    }

    private boolean pull(Git git) {
        PullCommand cmd = git.pull();
        if (isAuth()) {
            cmd.setCredentialsProvider(new UsernamePasswordCredentialsProvider(getUserId(), getPassword()));
        }
        try {
            PullResult res = cmd.call();
            if (!res.isSuccessful()) {
                Logger.getLogger(AxoGitLibrary.class.getName()).log(Level.WARNING, "Sync (pull) FAILED : {0}", logDetails());
                return false;
            }
            return true;

        } catch (GitAPIException ex) {
            Logger.getLogger(AxoGitLibrary.class.getName()).log(Level.WARNING, "Sync (pull) FAILED : {0}", logDetails());
            Logger.getLogger(AxoGitLibrary.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    private boolean checkout(Git git) {
        String branch = getBranch();

        // check to see if already checked out
        try {
            if (branch.equals(git.getRepository().getBranch())) {
                return true;
            }
        } catch (IOException ex) {
            Logger.getLogger(AxoGitLibrary.class.getName()).log(Level.WARNING, "Sync (check local branch) FAILED : {0}", logDetails());
            Logger.getLogger(AxoGitLibrary.class.getName()).log(Level.SEVERE, null, ex);
        }

        // check to see if branch is already available locally
        String localref = "refs/heads/" + branch;
        boolean localAvailable = false;
        List<Ref> bl_call;
        try {
            bl_call = git.branchList().call();
            for (Ref ref : bl_call) {
                if (ref.getName().equals(localref)) {
                    localAvailable = true;
                }
            }
        } catch (GitAPIException ex) {
            Logger.getLogger(AxoGitLibrary.class.getName()).log(Level.WARNING, "Sync (branch list) FAILED : {0}", logDetails());
            Logger.getLogger(AxoGitLibrary.class.getName()).log(Level.SEVERE, null, ex);
        }

        CheckoutCommand cmd = git.checkout();
        cmd.setName(branch);
        if (!localAvailable) {
            // create local branch pointing to remote branch
            cmd.setCreateBranch(true);
            cmd.setUpstreamMode(CreateBranchCommand.SetupUpstreamMode.SET_UPSTREAM);
            cmd.setStartPoint("origin/" + branch);
        }
        try {
            cmd.call();
            CheckoutResult res = cmd.getResult();
            if (!res.getStatus().equals(CheckoutResult.Status.OK)) {
                Logger.getLogger(AxoGitLibrary.class.getName()).log(Level.WARNING, "Sync (checkout) FAILED : {0}", logDetails());
                return false;
            }
            return true;
        } catch (GitAPIException ex) {
            Logger.getLogger(AxoGitLibrary.class.getName()).log(Level.WARNING, "Sync (checkout) FAILED : {0}", logDetails());
            Logger.getLogger(AxoGitLibrary.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;

    }

    private boolean add(Git git) {
        AddCommand cmd = git.add();
        if (getContributorPrefix() != null && getContributorPrefix().length() > 0) {
            cmd.addFilepattern("objects/" + getContributorPrefix());
            cmd.addFilepattern("patches/" + getContributorPrefix());
        } else {
            cmd.addFilepattern(".");
        }
        cmd.setUpdate(false);
        try {
            cmd.call();
            return true;
        } catch (GitAPIException ex) {
            Logger.getLogger(AxoGitLibrary.class.getName()).log(Level.WARNING, "Sync (add) FAILED : {0}", logDetails());
            Logger.getLogger(AxoGitLibrary.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;

    }

    private boolean commit(Git git) {
        CommitCommand cmd = git.commit();
        cmd.setAll(true);
        cmd.setMessage("commit from axoloti UI");
        cmd.setAllowEmpty(false);
        try {
            RevCommit rev = cmd.call();
            return true;
        } catch (GitAPIException ex) {
            Logger.getLogger(AxoGitLibrary.class.getName()).log(Level.WARNING, "Sync (commit) FAILED : {0}", logDetails());
            Logger.getLogger(AxoGitLibrary.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    private boolean reportStatus(Git git) {
        StatusCommand cmd = git.status();
        try {
            String overallStatus = "OK";
            Status status = cmd.call();

            StringBuilder details = new StringBuilder();

            try {
                details.append(git.getRepository().getBranch());
            } catch (IOException ex) {
                details.append("branch error");
            }
            details.append(",");
            if (status.isClean()) {
                details.append("clean");
            } else {
                details.append("dirty");
            }

            Logger.getLogger(AxoGitLibrary.class.getName()).log(Level.INFO, "Status: {0} : {1}  ( {2} )", new Object[]{logDetails(), overallStatus, details.toString()});
            if (!status.isClean()) {
                Logger.getLogger(AxoGitLibrary.class.getName()).log(Level.INFO, "Changes for: {0}", logDetails());
                for (String f : status.getAdded()) {
                    Logger.getLogger(AxoGitLibrary.class.getName()).log(Level.INFO, "added: {0} ", f);
                }
                for (String f : status.getChanged()) {
                    Logger.getLogger(AxoGitLibrary.class.getName()).log(Level.INFO, "changed: {0} ", f);
                }
                for (String f : status.getConflicting()) {
                    Logger.getLogger(AxoGitLibrary.class.getName()).log(Level.INFO, "conflicting: {0} ", f);
                }
                for (String f : status.getMissing()) {
                    Logger.getLogger(AxoGitLibrary.class.getName()).log(Level.INFO, "missing: {0} ", f);
                }
                for (String f : status.getModified()) {
                    Logger.getLogger(AxoGitLibrary.class.getName()).log(Level.INFO, "modified: {0} ", f);
                }
                for (String f : status.getRemoved()) {
                    Logger.getLogger(AxoGitLibrary.class.getName()).log(Level.INFO, "removed: {0} ", f);
                }
                for (String f : status.getUntracked()) {
                    Logger.getLogger(AxoGitLibrary.class.getName()).log(Level.INFO, "untracked: {0} ", f);
                }
                for (String f : status.getUntrackedFolders()) {
                    Logger.getLogger(AxoGitLibrary.class.getName()).log(Level.INFO, "untracked folders: {0} ", f);
                }
                for (String f : status.getUncommittedChanges()) {
                    Logger.getLogger(AxoGitLibrary.class.getName()).log(Level.INFO, "uncommited: {0} ", f);
                }
            }
            return true;
        } catch (GitAPIException ex) {
            Logger.getLogger(AxoGitLibrary.class.getName()).log(Level.SEVERE, null, ex);
            Logger.getLogger(AxoGitLibrary.class.getName()).log(Level.INFO, "Status: exception  {0}", logDetails());
        }
        return false;
    }

    private boolean push(Git git) {
        PushCommand cmd = git.push();
        if (isAuth()) {
            cmd.setCredentialsProvider(new UsernamePasswordCredentialsProvider(getUserId(), getPassword()));
        }

        try {
            Iterable<PushResult> res = cmd.call();
            return true;
        } catch (GitAPIException ex) {
            Logger.getLogger(AxoGitLibrary.class.getName()).log(Level.WARNING, "Sync (push) FAILED : {0}", logDetails());
            Logger.getLogger(AxoGitLibrary.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    private boolean isDirty(Git git) {
        try {
            StatusCommand cmd = git.status();
            String pre = "";
            if (getContributorPrefix() != null && getContributorPrefix().length() > 0) {
                pre = getContributorPrefix() + "/";
            }
            cmd.addPath("objects/" + pre);
            cmd.addPath("patches/" + pre);
            Status status = cmd.call();
            return !status.isClean();
        } catch (GitAPIException ex) {
            Logger.getLogger(AxoGitLibrary.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoWorkTreeException ex) {
            Logger.getLogger(AxoGitLibrary.class.getName()).log(Level.WARNING, "Sync (isdirty) FAILED : {0}", logDetails());
            Logger.getLogger(AxoGitLibrary.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    private boolean usingSubmodule() {
        // are we in developer mode, and not pointing elsewhere
        return (Axoloti.isDeveloper()
                && getLocalLocation().startsWith(System.getProperty(Axoloti.RELEASE_DIR))
                && (getId().equals(AxolotiLibrary.FACTORY_ID) || getId().equals(AxolotiLibrary.USER_LIBRARY_ID)));
    }

    private boolean isAuth() {
        return getUserId() != null && getUserId().length() > 0;
    }

    private String logDetails() {
        StringBuilder str = new StringBuilder();
        str.append(getId()).append(" (").append(getBranch());
        if (getUserId() == null || getUserId().length() == 0) {
            str.append(",anon");
        } else {
            str.append(",").append(getUserId());
        }
        str.append(")");
        return str.toString();
    }
}
