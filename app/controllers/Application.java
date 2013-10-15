package controllers;

import play.*;
import play.mvc.*;

import views.html.*;

public class Application extends Controller {

    public static Result index() {
        return ok(index.render("SNO2"));
    }

    public static Result test() {
        return ok(index.render("Test SNO2"));
    }

}
