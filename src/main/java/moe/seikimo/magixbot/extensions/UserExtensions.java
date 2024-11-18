package moe.seikimo.magixbot.extensions;

import moe.seikimo.data.DatabaseUtils;
import moe.seikimo.magixbot.data.models.UserModel;
import net.dv8tion.jda.api.entities.User;

public final class UserExtensions {
    /**
     * @return The user model, from the database or a new instance.
     */
    public static UserModel getData(User user) {
        var data = DatabaseUtils.fetch(
                UserModel.class, "userId", user.getId());
        if (data == null) {
            data = new UserModel();
            data.setUserId(user.getId());
        }

        return data;
    }
}
