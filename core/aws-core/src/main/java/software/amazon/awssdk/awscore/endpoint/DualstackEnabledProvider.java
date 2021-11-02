package software.amazon.awssdk.awscore.endpoint;

import java.util.Optional;
import java.util.function.Supplier;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.core.SdkSystemSetting;
import software.amazon.awssdk.profiles.ProfileFile;
import software.amazon.awssdk.profiles.ProfileFileSystemSetting;
import software.amazon.awssdk.profiles.ProfileProperty;
import software.amazon.awssdk.utils.Validate;

@SdkProtectedApi
public class DualstackEnabledProvider {
    private final Supplier<ProfileFile> profileFile;
    private final String profileName;

    private DualstackEnabledProvider(Builder builder) {
        this.profileFile = Validate.paramNotNull(builder.profileFile, "profileFile");
        this.profileName = builder.profileName;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Optional<Boolean> isDualstackEnabled() {
        Optional<Boolean> setting = SdkSystemSetting.AWS_USE_DUALSTACK_ENDPOINT.getBooleanValue();
        if (setting.isPresent()) {
            return setting;
        }

        return profileFile.get()
                          .profile(profileName())
                          .flatMap(p -> p.booleanProperty(ProfileProperty.USE_DUALSTACK_ENDPOINT));
    }

    private String profileName() {
        return profileName != null ? profileName : ProfileFileSystemSetting.AWS_PROFILE.getStringValueOrThrow();
    }

    public static final class Builder {
        private Supplier<ProfileFile> profileFile = ProfileFile::defaultProfileFile;
        private String profileName;

        private Builder() {}

        public Builder profileFile(Supplier<ProfileFile> profileFile) {
            this.profileFile = profileFile;
            return this;
        }

        public Builder profileName(String profileName) {
            this.profileName = profileName;
            return this;
        }

        public DualstackEnabledProvider build() {
            return new DualstackEnabledProvider(this);
        }
    }
}
