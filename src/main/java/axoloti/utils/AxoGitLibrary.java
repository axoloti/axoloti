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
                    Logger.getLogger(AxoGitLibrary.class.getName()).log(Level.WARNING, "sync repo FAILED cannot find submodule : {0}", getId());
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
                return;
            }
            boolean isDirty = isDirty(git);
            if (isDirty && isAuth()) {
                Logger.getLogger(AxoGitLibrary.class.getName()).log(Level.INFO, "Modifications detected : {0}", getId());
                if (!add(git)) {
                    return;
                }
                if (!commit(git)) {
                    return;
                }
                if (!push(git)) {
                    return;
                }
                Logger.getLogger(AxoGitLibrary.class.getName()).log(Level.INFO, "Modifications uploaded : {0}", getId());
            }
            if (!checkout(git)) {
                return;
            }
            Logger.getLogger(AxoGitLibrary.class.getName()).log(Level.INFO, "Sync Successful : {0}", getId());
        }
    }

    @Override
    public void init(boolean delete) {
        File ldir = new File(getLocalLocation());

        if (!usingSubmodule()) {
            if (getRemoteLocation() == null || getRemoteLocation().length() == 0) {
                Logger.getLogger(AxoGitLibrary.class.getName()).log(Level.WARNING, "init FAILED - no remote specified : {0}", getId());
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

            CloneCommand cmd = Git.cloneRepository();
            cmd.setURI(getRemoteLocation());
            cmd.setDirectory(ldir);
            if (isAuth()) {
                cmd.setCredentialsProvider(new UsernamePasswordCredentialsProvider(getUserId(), getPassword()));
            }
            try {
                cmd.call();
                Logger.getLogger(AxoGitLibrary.class.getName()).log(Level.INFO, "Repo initialised Successfully : {0}", getId());
            } catch (Exception ex) {
                Logger.getLogger(AxoGitLibrary.class.getName()).log(Level.WARNING, "init repo FAILED : {0}", getId());
                Logger.getLogger(AxoGitLibrary.class.getName()).log(Level.SEVERE, null, ex);
                return;
            }
        } else {
            Logger.getLogger(AxoGitLibrary.class.getName()).log(Level.INFO, "Developer mode do NOT clone repo : {0}", getId());
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
                Logger.getLogger(AxoGitLibrary.class.getName()).log(Level.WARNING, "Sync (pull) FAILED : {0}", getId());
                return false;
            }
            return true;

        } catch (GitAPIException ex) {
            Logger.getLogger(AxoGitLibrary.class.getName()).log(Level.WARNING, "Sync (pull) FAILED : {0}", getId());
            Logger.getLogger(AxoGitLibrary.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    private boolean checkout(Git git) {
        String rev = getRevision();
        if (rev == null || rev.length() == 0) {
            rev = "master";
        }

        // check to see if already checked out
        try {
            if (rev.equals(git.getRepository().getBranch())) {
                return true;
            }
        } catch (IOException ex) {
            Logger.getLogger(AxoGitLibrary.class.getName()).log(Level.WARNING, "Sync (check local branch) FAILED : {0}", getId());
            Logger.getLogger(AxoGitLibrary.class.getName()).log(Level.SEVERE, null, ex);
        }

        // check to see if branch is already available locally
        String localref = "refs/heads/" + rev;
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
            Logger.getLogger(AxoGitLibrary.class.getName()).log(Level.WARNING, "Sync (branch list) FAILED : {0}", getId());
            Logger.getLogger(AxoGitLibrary.class.getName()).log(Level.SEVERE, null, ex);
        }

        CheckoutCommand cmd = git.checkout();
        cmd.setName(rev);
        if (!localAvailable) {
            // create local branch pointing to remote branch
            cmd.setCreateBranch(true);
            cmd.setUpstreamMode(CreateBranchCommand.SetupUpstreamMode.SET_UPSTREAM);
            cmd.setStartPoint("origin/" + rev);
        }
        try {
            cmd.call();
            CheckoutResult res = cmd.getResult();
            if (!res.getStatus().equals(CheckoutResult.Status.OK)) {
                Logger.getLogger(AxoGitLibrary.class.getName()).log(Level.WARNING, "Sync (checkout) FAILED : {0}", getId());
                return false;
            }
            return true;
        } catch (GitAPIException ex) {
            Logger.getLogger(AxoGitLibrary.class.getName()).log(Level.WARNING, "Sync (checkout) FAILED : {0}", getId());
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
            Logger.getLogger(AxoGitLibrary.class.getName()).log(Level.WARNING, "Sync (add) FAILED : {0}", getId());
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
            Logger.getLogger(AxoGitLibrary.class.getName()).log(Level.WARNING, "Sync (commit) FAILED : {0}", getId());
            Logger.getLogger(AxoGitLibrary.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(AxoGitLibrary.class.getName()).log(Level.WARNING, "Sync (push) FAILED : {0}", getId());
            Logger.getLogger(AxoGitLibrary.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    private boolean isDirty(Git git) {
        try {
            StatusCommand cmd = git.status();
            String pre = "";
            if (getContributorPrefix() != null && getContributorPrefix().length() > 0) {
                pre = getContributorPrefix() + File.separator;
            }
            cmd.addPath("objects" + File.separator + pre);
            cmd.addPath("patches" + File.separator + pre);
            Status status = cmd.call();
            if (status.isClean()) {
                return false;
            }
            return true;
        } catch (GitAPIException ex) {
            Logger.getLogger(AxoGitLibrary.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoWorkTreeException ex) {
            Logger.getLogger(AxoGitLibrary.class.getName()).log(Level.WARNING, "Sync (status) FAILED : {0}", getId());
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
}
