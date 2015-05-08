package logics.template;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;
import controllers.Helper;
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

import static java.lang.Long.*;
import static logics.template.Catalog.addQaToCatalog;
import static logics.template.Variables.addVarsToQA;

/**
 * Created by corina on 09.04.2015.
 */
public class QualityAttribute {
    static CatalogQADAO catalogQADAO = new CatalogQADAO();
    static CatalogDAO catalogDAO = new CatalogDAO();
    static QACategoryDAO qaCategoryDAO = new QACategoryDAO();
    static QualityAttributeDAO qaDAO = new QualityAttributeDAO();
    static models.template.Catalog defaultCatalog;

    static {
        models.template.Catalog tmp = null;
        try {
            tmp = catalogDAO.readById(parseLong("6000"));
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
        }
        defaultCatalog = tmp;
    }

    public static QA createQA(int versionNumber, JsonNode json) throws MissingParameterException, EntityNotFoundException {
        String qaText = json.findValue("description").asText();
        if (qaText.equals("")) {
            throw new MissingParameterException("QualityAttribute text can not be emtpy");
        }
        else {
            QA qa = new QA(qaText, versionNumber);
            Logger.info("before first persost");
            qa = qaDAO.persist(qa);
            qa = setCategoriesInQa(qa, json);
            Logger.info("after categories persist" + qa.getId());
            CatalogQA catalogQA = addQaToCatalog(qa, defaultCatalog);
            Logger.info("after catalog.update");
            addVarsToQA(catalogQA, json);
            Logger.info("after catalogQAdao.update");
            return qa;
        }
    }

    public static List<QA> getAllQAs() {
        return qaDAO.readAllLatest();
    }

    private static QA setCategoriesInQa(QA qa, JsonNode json) throws EntityNotFoundException {
        JsonNode categoriesNode = json.findValue("categories");
        List<String> list = categoriesNode.findValuesAsText("id");
        List<Long> categoryIds = Lists.transform(list, Helper.parseLongFunction());
        List<QACategory> qaCategories = qaCategoryDAO.readAllById(categoryIds);
        qa.getCategories().clear();
        qa.addCategories(qaCategories);
        return qaDAO.update(qa);
    }

    public static List<CatalogQA> getQAsByCatalog(long id) throws EntityNotFoundException {
        models.template.Catalog cat = catalogDAO.readById(id);
        return catalogQADAO.findByCatalog(cat);
    }

    public static void deleteQA(Long id) throws EntityNotFoundException {
        QA qa = qaDAO.readById(id);
        List<CatalogQA> catalogQAList = catalogQADAO.findAllByQA(qa);
        for (CatalogQA catalogQA : catalogQAList) {
            markAsDeleted(catalogQA);
        }
        qa.setIsDeleted(true);
        qaDAO.update(qa);
    }

    private static void markAsDeleted(CatalogQA catalogQA) {
        catalogQA.setIsDeleted(true);
        catalogQADAO.update(catalogQA);
    }


    public static void updateQA(Long oldQaId, Long id) {
    }

    public static QA updateQA(JsonNode json) throws EntityNotFoundException, MissingParameterException {
        Long oldQAId = json.findValue("id").asLong();
        QA currentQA = qaDAO.readById(oldQAId);
        Long catalogId = json.findValue("catalog").asLong();

        if (currentQA.getDescription().equals(json.findValue("description").asText())) {
            setCategoriesInQa(currentQA, json);
            addVarsToQA(catalogQADAO.findByCatalogAndId(defaultCatalog, currentQA), json);
        } else {
            QA newQA = qaDAO.persist(createQA(currentQA.getVersionNumber() + 1, json));
            List<CatalogQA> catalogQAList = catalogQADAO.findAllByQA(currentQA);
            for (CatalogQA catalogQA : catalogQAList) {
                markAsDeleted(catalogQA);
                if (catalogQA.getCatalog().getId() != defaultCatalog.getId()) {
                    addQaToCatalog(newQA, catalogQA.getCatalog());
                }
            }
            currentQA.setIsDeleted(true);
            qaDAO.update(currentQA);
            return newQA;
        }
        return null;
    }


}