package actions;

import controllers.Application;
import models.User;
import org.apache.commons.lang3.StringUtils;
import play.libs.F;
import play.mvc.Action;
import play.mvc.Http;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;


public class CurrentUser extends Action.Simple {


    @Override
    public F.Promise<play.mvc.SimpleResult> call(Http.Context ctx) throws Throwable{
        accessUser(ctx);
        return delegate.call(ctx);
    }


    private static User accessUser(Http.Context ctx)
    {
        User user = (User)ctx.args.get("user");
        if (user == null)
        {
            String username = ctx.session().get("username");
            if (!StringUtils.isEmpty(username))
            {
                user = new User();
                try {
                    Connection connection = Application.getDBConnection();
                    try {
                        Statement statement = Application.createStatement(connection);
                        user.set_EmployeeClass(statement,username);
                    } finally {
                        connection.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                if (user != null)
                {
                    ctx.args.put("user", user);
                }
            }
        }

        return user;
    }

    public static User currentUser()
    {
        return currentUser(Http.Context.current());
    }

    public static User currentUser(Http.Context context)
    {
        return accessUser(context);
    }

}