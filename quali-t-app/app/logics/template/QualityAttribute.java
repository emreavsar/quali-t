package logics.template;

import com.fasterxml.jackson.databind.JsonNode;
import dao.models.CatalogDAO;
import dao.models.CatalogQADAO;
import dao.models.QACategoryDAO;
import dao.models.QualityAttributeDAO;
import exceptions.EntityNotFoundException;
import exceptions.MissingParameterException;
import models.template.CatalogQA;
import models.template.QA;
import models.template.QACategory;
import play.Logger;

import java.util.List;

import static logics.template.Catalog.addQaToCatalog;
import static logics.template.Variables.addVarsToQA;

/**
 * Created by corina on 09.04.2015.
 */
public class QualityAttribute {
    public static QA createQA(String qaText, List<Long> categoryIds, JsonNode vars) throws MissingParameterException, EntityNotFoundException {
        if (qaText.equals("")) {
            throw new MissingParameterException("QualityAttribute text can not be emtpy");
        }
        else {
            QualityAttributeDAO qaDAO = new QualityAttributeDAO();
            QACategoryDAO qaCategoryDAO = new QACategoryDAO();

            List<QACategory> qaCategories = qaCategoryDAO.readAllById(categoryIds);
            QA qa = qaDAO.persist(new QA(qaText, qaCategories));
            CatalogQA catalogQA = addQaToCatalog(qa.getId(), Long.valueOf(6000));
            Logger.info("Values Node:   " + vars.toString());
            addVarsToQA(catalogQA, vars);
            return qa;
        }
    }

    public static List<QA> getAllQAs() {
        QualityAttributeDAO qaDAO = new QualityAttributeDAO();
        return qaDAO.readAll();
    }

    public static List<CatalogQA> getQAsByCatalog(long id) throws EntityNotFoundException {
        CatalogQADAO catqaDAO = new CatalogQADAO();
        CatalogDAO catalogDAO = new CatalogDAO();
        models.template.Catalog cat = catalogDAO.readById(id);
        return catqaDAO.findByCatalog(cat);
    }

//    public static QACategory createSubCat(Long id, String name) throws EntityNotFoundException {
//        QACategoryDAO catDao = new QACategoryDAO();
//        QACategory parent = catDao.readById(id);
//        QACategory cat = new QACategory(parent, name);
//        catDao.persist(parent);
//        return cat;
//    }

//    public static QACategory createCat(String name) {
//        QACategory cat = new QACategory(name);
//        QACategoryDAO catDao = new QACategoryDAO();
//        return catDao.persist(cat);
//    }

    public static QACategory getCategoryTree(Long id) throws EntityNotFoundException {
        QACategoryDAO catDAO = new QACategoryDAO();
        return catDAO.readById(id);
    }

    public static List<QACategory> getAllCats() {
        QACategoryDAO catDAO = new QACategoryDAO();
        return catDAO.readAllSuperclasses();
    }

    public static QACategory createCat(String name, Long parentid, String icon) throws EntityNotFoundException {
        Logger.info("logic called. name: " + name + " parent: " + parentid + " icon:  " + icon);
        QACategoryDAO catDao = new QACategoryDAO();

        if (parentid == null) {
            QACategory cat = new QACategory(name, icon);
            return catDao.persist(cat);
        } else  {
            QACategory parent = catDao.readById(parentid);
            QACategory cat = new QACategory(parent, name, icon);
            catDao.persist(parent);
            return cat;
        }
    }

    public static void deleteCategory(Long id) throws EntityNotFoundException {
        QACategoryDAO qaCategoryDAO = new QACategoryDAO();
        QACategory category = getCategoryTree(id);
        qaCategoryDAO.remove(category);
    }

    public static QACategory updateCat(Long id, String name, String icon) throws EntityNotFoundException {
        QACategoryDAO qaCategoryDAO = new QACategoryDAO();
        QACategory category = qaCategoryDAO.readById(id);
        category.setName(name);
        category.setIcon(icon);
        return qaCategoryDAO.persist(category);
    }
}