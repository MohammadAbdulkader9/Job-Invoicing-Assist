package com.mdu.jobinvoicing;

import java.io.Serializable;

public class LoadTicket implements Serializable {
    public String created, targetName, creatorTargetName, operator, weight, freeText;
    public String materialNames, loadCount, localId, customer, projectReference;

    public LoadTicket(String created, String targetName, String creatorTargetName,
                      String operator, String weight, String freeText, String materialNames,
                      String loadCount, String localId, String customer, String projectReference) {
        this.customer = customer;
        this.projectReference = projectReference;
        this.materialNames = materialNames;
        this.weight = weight;
        this.operator = operator;
        this.targetName = targetName;
        this.creatorTargetName = creatorTargetName;
        this.freeText = freeText;
        this.loadCount = loadCount;
        this.localId = localId;
        this.created = created;

    }
}
