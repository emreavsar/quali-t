package controllers;

import be.objectify.deadbolt.java.actions.Group;
import be.objectify.deadbolt.java.actions.Restrict;
import be.objectify.deadbolt.java.actions.SubjectPresent;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;
import exceptions.EntityNotFoundException;
import exceptions.MissingParameterException;
import models.template.QACategory;
import org.apache.commons.lang3.StringUtils;
import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.List;


/**
 * Created by corina on 09.04.2015.
 */
public class QualityAttribute extends Controller {

    @Restrict({@Group("curator"), @Group("admin")})
    @Transactional
    public static Result createQA() throws MissingParameterException {
        JsonNode json = request().body().asJson();

        String qaText = json.findValue("description").asText();
        JsonNode node = json.findValue("categories");
        List<String> list = node.findValuesAsText("id");
        List<Long> categories = Lists.transform(list, Helper.parseLongFunction());
        JsonNode vars = json.findValue("variables");
        try {
            return ok(Json.toJson(logics.template.QualityAttribute.createQA(qaText, categories, vars)));
        } catch (MissingParameterException e) {
            return status(400, e.getMessage());
        } catch (EntityNotFoundException e) {
            return status(400, e.getMessage());
        }
    }

    @SubjectPresent
    @Transactional
    public static Result getAllQAs() {
        return ok(Json.toJson(logics.template.QualityAttribute.getAllQAs()));
    }


    @SubjectPresent
    @Transactional
    public static Result getQAsByCatalog(long id) {
        try {
            return ok(Json.toJson(logics.template.QualityAttribute.getQAsByCatalog(id)));
        } catch (EntityNotFoundException e) {
            return status(400, e.getMessage());
        }
    }

    // replaced by createCat
//    @Restrict({@Group("curator"),@Group("admin")})
//    @Transactional
//    public static Result createSubCat(Long id, String name) {
//        try {
//            QACategory cat = logics.template.QualityAttribute.createSubCat(id, name);
//            return ok(Json.toJson(cat));
//        } catch (EntityNotFoundException e) {
//            return status(400, e.getMessage());
//        }
//    }

    @SubjectPresent
    @Transactional
    public static Result getCategoryTree(long id) {
        try {
            return ok(Json.toJson(logics.template.QualityAttribute.getCategoryTree(id)));
        } catch (EntityNotFoundException e) {
            return status(400, e.getMessage());
        }
    }

    @SubjectPresent
    @Transactional
    public static Result getAllCats() {
        List<QACategory> cats = logics.template.QualityAttribute.getAllCats();
        return ok(Json.toJson(cats));
    }

    @Restrict({@Group("curator"), @Group("admin")})
    @Transactional
    public static Result createCat() {
        Logger.info("creatcat controller called");
        DynamicForm requestData = Form.form().bindFromRequest();
        String name = requestData.get("name");
        String icon = requestData.get("icon");
        String parent = requestData.get("parent");
        if (StringUtils.isBlank(parent)) {
            try {
                return ok(Json.toJson(logics.template.QualityAttribute.createCat(name, null, icon)));
            } catch (EntityNotFoundException e) {
                return status(400, e.getMessage());
            }
        } else {
            Long parentid = Long.parseLong(parent);
            try {
                return ok(Json.toJson(logics.template.QualityAttribute.createCat(name, parentid, icon)));
            } catch (EntityNotFoundException e) {
                return status(400, e.getMessage());
            }
        }
    }

    @Restrict({@Group("curator"), @Group("admin")})
    @Transactional
    public static Result deleteCat(Long id) {
        try {
            logics.template.QualityAttribute.deleteCategory(id);
            return status(202);
        } catch (EntityNotFoundException e) {
            return status(400, e.getMessage());
        }
    }

    @Restrict({@Group("curator"), @Group("admin")})
    @Transactional
    public static Result updateCat() {
        DynamicForm requestData = Form.form().bindFromRequest();
        Long id = Long.parseLong(requestData.get("id"));
        String name = requestData.get("name");
        String icon = requestData.get("icon");
        try {
            return ok(Json.toJson(logics.template.QualityAttribute.updateCat(id, name, icon)));
        } catch (EntityNotFoundException e) {
            return status(400, e.getMessage());
        }
    }
}
