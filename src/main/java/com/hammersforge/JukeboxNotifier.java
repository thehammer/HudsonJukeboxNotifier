package com.hammersforge;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Notifier;
import hudson.tasks.Publisher;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import net.sf.json.JSONObject;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

public class JukeboxNotifier extends Notifier {
	public String jukeboxURL = "";
	public String snippetName = "";

	/** the Logger */
    private static java.util.logging.Logger log = java.util.logging.Logger.getLogger(JukeboxNotifier.class.getName());
    
    @DataBoundConstructor
    public JukeboxNotifier(String jukeboxURL, String snippetName) {
    	this.jukeboxURL = jukeboxURL;
    	this.snippetName = snippetName;
    	
    	log.info("######################################");
    	log.info("# Initializing HudsonJukeboxNotifier #");
    	log.info("######################################");
    }

    public String getJukeboxURL() {
        return jukeboxURL;
    }
    
    public String getSnippetName() {
    	return snippetName;
    }
    
    private void playHammertime(String snippet_name) {
		HttpURLConnection jukebox = null;

		try {
			jukebox = (HttpURLConnection) new URL("http://10.103.180.203:3000/hammertime/add_for/" + snippet_name).openConnection();
			jukebox.setRequestMethod("POST");
			jukebox.getContent();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		} finally {
			if (jukebox != null) {
				jukebox.disconnect();
			}
		}
	}
    
    @Override
    public boolean prebuild(final AbstractBuild<?, ?> build, BuildListener listener) {
        return true;
    }

    @Override
    public boolean perform(final AbstractBuild<?, ?> build, final Launcher launcher, final BuildListener listener) throws InterruptedException, IOException {
    	log.info("Received perform call.");
    	if ((build.getResult() == Result.FAILURE) || (build.getResult() == Result.UNSTABLE)) {
    		log.info("Detected a failure");
			playHammertime("claire_build_failed");
		}
		
		return true;
    }

	public BuildStepMonitor getRequiredMonitorService() {
		return BuildStepMonitor.NONE;
	}
	
    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl)super.getDescriptor();
    }

    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Publisher> {
        public DescriptorImpl() {
            load();
        }

        @Override
        public String getDisplayName() {
            return "Jukebox Notifier";
        }

        @Override
        public boolean isApplicable(Class type) {
            return true;
        }

        @Override
        public boolean configure(StaplerRequest staplerRequest, JSONObject json) throws FormException {
            save();
            return true;
        }
    }

}