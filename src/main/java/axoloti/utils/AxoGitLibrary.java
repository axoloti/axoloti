package axoloti.utils;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.errors.GitAPIException;

// will derive off AxolotiLibrary
public class AxoGitLibrary {

    public AxoGitLibrary(AxolotiLibrary lib) {
        library = lib;
        try {
            gitcmd = Git.open(new File(lib.getLocalLocation()));
        } catch (IOException ex) {
            Logger.getLogger(AxoGitLibrary.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void sync() {
        if (gitcmd != null) {
            try {
                PullResult res = gitcmd.pull().call();
                if (res.isSuccessful()) {
                    Logger.getLogger(AxoGitLibrary.class.getName()).log(Level.INFO, "Sync Successful : {0}", library.getId());
                } else {
                    Logger.getLogger(AxoGitLibrary.class.getName()).log(Level.WARNING, "Sync FAILED : {0}", library.getId());
                }
            } catch (GitAPIException ex) {
                Logger.getLogger(AxoGitLibrary.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    AxolotiLibrary library;
    Git gitcmd;
}
