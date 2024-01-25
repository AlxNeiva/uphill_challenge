package com.uphill.challenge;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import com.google.common.io.Resources;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.*;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class FHIRProcessing
{
    public String processResource(String resourceID, String baseResource)
    {
        String resourceObj = "";
        String finalObj = "";
        FhirContext ctx = FhirContext.forR4();
        IParser parser = ctx.newJsonParser();

        InputStream is = FHIRProcessing.class.getResourceAsStream("/fhir_resources/" + baseResource + ".json");

        try
        {
            resourceObj = new String(is.readAllBytes(), StandardCharsets.UTF_8);

        } catch (IOException e)
        {
            finalObj = parser.encodeResourceToString(((IBaseResource)operationOutcome("","Some error occurred when trying to read the Resource!")));
        }

        //--PARSE BUNDLE
        var parsed = parser.parseResource(resourceObj);

        if(!parsed.isEmpty())
        {
            if(baseResource.equals("encounter"))
                finalObj = parser.encodeResourceToString(processEncounter(parsed, resourceID));
            if(baseResource.equals("patient"))
                finalObj = parser.encodeResourceToString(processPatient(parsed, resourceID));
        }
        else
            finalObj = parser.encodeResourceToString(((IBaseResource)operationOutcome("","")));

        return finalObj;
    }

    public IBaseResource processEncounter(IBaseResource parsed, String resourceID)
    {
        var isValid = false;

        if (parsed instanceof org.hl7.fhir.r4.model.Encounter)
        {
            List<Identifier> lstEnc = ((Encounter) parsed).getIdentifier();

            if(lstEnc.size() >= 1)
            {
                for ( Identifier ident : lstEnc)
                {
                    if(ident.getSystem().equals("urn:uh-encounter-id") && ident.getValue().equals(resourceID))
                        isValid = true;
                }
            }

        }

        if(isValid)
            return ((IBaseResource) parsed);
        else
            return ((IBaseResource) operationOutcome(resourceID,""));
    }

    public IBaseResource processPatient(IBaseResource parsed, String resourceID)
    {
        var isValid = false;

        if (parsed instanceof org.hl7.fhir.r4.model.Patient)
        {
            List<Identifier> lstEnc = ((Patient) parsed).getIdentifier();

            if(lstEnc.size() >= 1)
            {
                for ( Identifier ident : lstEnc)
                {
                    if(ident.getSystem().equals("urn:uh-patient-id") && ident.getValue().equals(resourceID))
                        isValid = true;
                }
            }

        }

        if(isValid)
            return ((IBaseResource) parsed);
        else
            return ((IBaseResource) operationOutcome(resourceID, ""));
    }

    public OperationOutcome operationOutcome(String id, String diagnostic)
    {
        OperationOutcome op = new OperationOutcome();
        OperationOutcome.OperationOutcomeIssueComponent opIssue = new OperationOutcome.OperationOutcomeIssueComponent();
        opIssue.setSeverity(OperationOutcome.IssueSeverity.ERROR);
        opIssue.setCode(OperationOutcome.IssueType.PROCESSING);
        if(diagnostic.isEmpty())
            opIssue.setDiagnostics("Resource with ID " + id + " does not exists!");
        else
            opIssue.setDiagnostics(diagnostic);

        op.addIssue(opIssue);

        return op;
    }
}
