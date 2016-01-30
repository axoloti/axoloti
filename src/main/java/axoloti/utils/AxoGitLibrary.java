package axoloti.utils;

import axoloti.Axoloti;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.submodule.SubmoduleWalk;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

public class AxoGitLibrary extends AxolotiLibrary {

    public AxoGitLibrary(String id, String type, String lloc, boolean e, String rloc, boolean auto) {
        super(id, type, lloc, e, rloc, auto);

    }

    public AxoGitLibrary() {
    }

    @Override
    public void sync() {
        Git git = null;
        try {
            FileRepositoryBuilder builder = new FileRepositoryBuilder();
            Repository repository;
            if (usingSubmodule()) {
                // special case, in developer mode, we have the repos as sub modules, these need to be accessed via the parent repo
                String relDir = System.getProperty(Axoloti.RELEASE_DIR);
                Git parent = Git.open(new File(relDir));
                repository = SubmoduleWalk.getSubmoduleRepository(parent.getRepository(), getId());
            } else {
                repository = Git.open(new File(getLocalLocation())).getRepository();
            }
            git = new Git(repository);

        } catch (IOException ex) {
            Logger.getLogger(AxoGitLibrary.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (git != null) {
            try {
                PullResult res = git.pull().call();
                if (res.isSuccessful()) {
                    Logger.getLogger(AxoGitLibrary.class.getName()).log(Level.INFO, "Sync Successful : {0}", getId());
                } else {
                    Logger.getLogger(AxoGitLibrary.class.getName()).log(Level.WARNING, "Sync FAILED : {0}", getId());
                }
            } catch (GitAPIException ex) {
                Logger.getLogger(AxoGitLibrary.class.getName()).log(Level.WARNING, "Sync FAILED : {0}", getId());
                Logger.getLogger(AxoGitLibrary.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public void init() {
        File ldir = new File(getLocalLocation());

        if (!usingSubmodule()) {
//        if(ldir.exists()) {
//            try {
//                delete(ldir);
//            } catch (IOException ex) {
//                Logger.getLogger(AxoGitLibrary.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
            if (!ldir.exists()) {
                ldir.mkdirs();
            }

            try {

                CloneCommand clone = Git.cloneRepository();
                clone.setURI(getRemoteLocation());
                clone.setDirectory(ldir);
                if (getUserId() != null && getUserId().length() > 0) {
                    clone.setCredentialsProvider(new UsernamePasswordCredentialsProvider(getUserId(), getPassword()));
                }
                clone.call();
                Logger.getLogger(AxoGitLibrary.class.getName()).log(Level.INFO, "Repo initialised Successfully : {0}", getId());
            } catch (GitAPIException ex) {
                Logger.getLogger(AxoGitLibrary.class.getName()).log(Level.WARNING, "init repo FAILED : {0}", getId());
                Logger.getLogger(AxoGitLibrary.class.getName()).log(Level.SEVERE, null, ex);
            }

        } else {
            Logger.getLogger(AxoGitLibrary.class.getName()).log(Level.INFO, "Developer mode do NOT init() repo : {0}", getId());
        }

    }

    private boolean usingSubmodule() {
        return (Axoloti.isDeveloper()
                && (getId().equals(AxolotiLibrary.FACTORY_ID) || getId().equals(AxolotiLibrary.USER_LIBRARY_ID)));
    }
}
