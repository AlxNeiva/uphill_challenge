package com.uphill.challenge;

import org.hl7.fhir.r4.model.Patient;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/uphill/api")
public class ApiController
{
    @GetMapping(value = "/encounter", produces="application/json")
    public String getEncounter(@RequestParam String identifier)
    {
        FHIRProcessing fhirProcess = new FHIRProcessing();
        return fhirProcess.processResource(identifier, "encounter");

    }

    @GetMapping(value = "/patient", produces="application/json")
    public String getPatient(@RequestParam String identifier)
    {
        FHIRProcessing fhirProcess = new FHIRProcessing();
        return fhirProcess.processResource(identifier, "patient");
    }

}
