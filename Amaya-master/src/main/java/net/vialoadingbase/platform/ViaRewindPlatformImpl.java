package net.vialoadingbase.platform;

import com.viaversion.viarewind.api.ViaRewindPlatform;
import net.vialoadingbase.ViaLoadingBase;

import java.io.File;
import java.util.logging.Logger;

public class ViaRewindPlatformImpl
        implements ViaRewindPlatform {
    public ViaRewindPlatformImpl(File directory) {
        this.init(new File(directory, "viarewind.yml"));
    }

    public Logger getLogger() {
        return ViaLoadingBase.LOGGER;
    }

    public File getDataFolder() {
        return null;
    }

}

