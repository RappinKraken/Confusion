package controllers;

import java.sql.*;
import java.util.List;

import models.Category;
import models.Product;
import models.Publisher;
import play.mvc.Security;
import views.html.*;
import models.User;
import play.mvc.Controller;
import play.mvc.Result;

import play.data.DynamicForm;
import play.data.Form;
import play.db.*;


import javax.sql.DataSource;

public class Application extends Controller {

    public static Connection connection;

    public static Result index() {
        if (session().get("username") != null){
            session().remove("username");
        }
        return ok(views.html.index.render("GameGo Inventory"));
    }

    public static Result login() throws SQLException {

        DynamicForm userForm = Form.form().bindFromRequest();
        String username = userForm.data().get("username");
        String password = userForm.data().get("password");

        if (username == "") {
            flash("error", "Missing Username");
            return redirect(routes.Application.index());
        } else if (password == "") {
            flash("error", "Missing Password");
            return redirect(routes.Application.index());
        }
        boolean resultOK = false;

            resultOK = User.isLogin(getDBConnection(), username, password);
            connection.close();

        if (!resultOK) {
            flash("error", "Incorrect username or password.");
            return redirect(routes.Application.index());
        }
        User user = new User();
        user.set_EmployeeClass(getDBConnection(),username);
        session("username", user.getUsername());
        List <Product> products = Product.findAllAvailable(getDBConnection());
        List <Publisher> publishers = Publisher.findAll(getDBConnection());
        List <Category> categories = Category.findAll(getDBConnection());
        connection.close();
        return ok(views.html.inventory.render(products,publishers,categories));
        }

    public static Result logout(){
        session().remove("username");
        return redirect(routes.Application.index());
    }

    //future product description page
    // as you can see I used spanish objects hahhaha
    @Security.Authenticated(UserAuth.class)
    public static Result show(long id) throws SQLException {
    	Publisher publicador = new Publisher();
        Product productos = new  Product(); 
        productos.setSpecific(getDBConnection(), id);
        publicador.findPublisher(getDBConnection(), (int) id);
          return ok(views.html.product.render(productos, publicador));
      }

    @Security.Authenticated(UserAuth.class)
    public static Result search() throws SQLException {
        String search = "";
        DynamicForm dataForm = Form.form().bindFromRequest();
        search = dataForm.data().get("search");
        List <Product> products = Product.findByName(getDBConnection(),search);
        List <Publisher> publishers = Publisher.findAll(getDBConnection());
        List <Category> categories = Category.findAll(getDBConnection());
        return ok(views.html.inventory.render(products,publishers,categories));
    }

    @Security.Authenticated(UserAuth.class)
    public static Result sortByName(String order) throws SQLException {
        List <Product> products = Product.sortByName(getDBConnection(), order);
        List <Publisher> publishers = Publisher.findAll(getDBConnection());
        List <Category> categories = Category.findAll(getDBConnection());
        return ok(views.html.inventory.render(products,publishers,categories));
    }

    @Security.Authenticated(UserAuth.class)
    public static Result sortByPrice(String order) throws SQLException {
        List <Product> products = Product.sortByPrice(getDBConnection(), order);
        List <Publisher> publishers = Publisher.findAll(getDBConnection());
        List <Category> categories = Category.findAll(getDBConnection());
        return ok(views.html.inventory.render(products,publishers,categories));
    }

    @Security.Authenticated(UserAuth.class)
    public static Result findByPublisher(Long pubId) throws SQLException {
        List <Product> products = Product.findByPublisher(getDBConnection(), pubId);
        List <Publisher> publishers = Publisher.findAll(getDBConnection());
        List <Category> categories = Category.findAll(getDBConnection());
        return ok(views.html.inventory.render(products,publishers,categories));
    }

    @Security.Authenticated(UserAuth.class)
    public static Result findByCategory(Long categoryId) throws SQLException {
        List <Product> products = Product.findByCategory(getDBConnection(), categoryId);
        List <Publisher> publishers = Publisher.findAll(getDBConnection());
        List <Category> categories = Category.findAll(getDBConnection());
        return ok(views.html.inventory.render(products,publishers,categories));
    }

    public static Statement getDBConnection() throws SQLException {
        DataSource ds = DB.getDataSource();
        connection = ds.getConnection();
        return connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
    }

    }