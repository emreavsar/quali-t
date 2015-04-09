package models.template;


import models.AbstractEntity;

import javax.annotation.Nullable;
import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by corina on 31.03.2015.
 */

@Entity
@Table(name = "catalogtemplate")
@Nullable
public class CatalogTemplate extends AbstractEntity {
    public CatalogTemplate() {
    }

    public CatalogTemplate(QualityAttribute QualityAttribute, Catalog catalog) {
        this.catalog = catalog;
        this.qa = QualityAttribute;
    }

    @ManyToOne(optional = true, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Catalog catalog;

    @ManyToOne(optional = true, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private QualityAttribute qa;

    @OneToMany(mappedBy = "template", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<QAVar> vars = new HashSet<>();

    public Set<QAVar> getVars() {
        return vars;
    }

    public void setVars(Set<QAVar> vars) {
        this.vars = vars;
    }

    public Catalog getCatalog() {
        return catalog;
    }

    public void setCatalog(Catalog catalogID) {
        this.catalog = catalogID;
    }

    public QualityAttribute getQa() {
        return qa;
    }

    public void setQa(QualityAttribute QAID) {
        this.qa = QAID;
    }
}
