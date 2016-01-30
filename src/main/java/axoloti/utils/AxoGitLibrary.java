package axoloti.utils;

import axoloti.Axoloti;
import static axoloti.Axoloti.RELEASE_DIR;
import axoloti.MainFrame;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.submodule.SubmoduleWalk;

// will derive off AxolotiLibrary
public class AxoGitLibrary {

    public AxoGitLibrary(AxolotiLibrary lib) {
        library = lib;
        try {
            FileRepositoryBuilder builder = new FileRepositoryBuilder();
            Repository repository;
            if (Axoloti.isDeveloper()
                    && (lib.getId().equals(AxolotiLibrary.FACTORY_ID) || lib.getId().equals(AxolotiLibrary.USER_LIBRARY_ID))) {
                // special case, in developer mode, we have the repos as sub modules, these need to be accessed via the parent repo
                String relDir = System.getProperty(Axoloti.RELEASE_DIR);
                Git parent = Git.open(new File(relDir));
                repository = SubmoduleWalk.getSubmoduleRepository(parent.getRepository(), lib.getId());
            } else {
                repository = git.open(new File(lib.getLocalLocation())).getRepository();
            }
            git = new Git(repository);
        } catch (IOException ex) {
            Logger.getLogger(AxoGitLibrary.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void sync() {
        if (git != null) {
            try {
                PullResult res = git.pull().call();
                if (res.isSuccessful()) {
                    Logger.getLogger(AxoGitLibrary.class.getName()).log(Level.INFO, "Sync Successful : {0}", library.getId());
                } else {
                    Logger.getLogger(AxoGitLibrary.class.getName()).log(Level.WARNING, "Sync FAILED : {0}", library.getId());
                }
            } catch (GitAPIException ex) {
                Logger.getLogger(AxoGitLibrary.class.getName()).log(Level.WARNING, "Sync FAILED : {0}", library.getId());
                Logger.getLogger(AxoGitLibrary.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    AxolotiLibrary library;
    Git git;
}
