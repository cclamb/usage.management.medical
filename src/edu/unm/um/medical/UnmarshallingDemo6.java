package edu.unm.um.medical;

import org.astm.ccr.*;
import org.w3._2000._09.xmldsig_.*;
import java.io.File;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.util.*;
import javax.xml.transform.stream.StreamSource;
public class UnmarshallingDemo6 {
	private String con;
	private String med; 
	private String stat;
	
   	private JAXBContext context;
   	private Unmarshaller unmarshaller;
   	private JAXBElement<ContinuityOfCareRecord> element; 
   	private ContinuityOfCareRecord sample;
    	public void extract(File doc) throws JAXBException {


	
	try{
	context = JAXBContext.newInstance("org.astm.ccr");
	unmarshaller =context.createUnmarshaller();

	element = unmarshaller.unmarshal(new StreamSource(doc),ContinuityOfCareRecord.class);

	sample = element.getValue();
	
	CodedDescriptionType langsamp = sample.getLanguage();
	
	List<CodedDescriptionType.ObjectAttribute> objattsamplist = langsamp.getObjectAttribute();
	
	//The list returned empty so skipping the following two lines
	//CodedDescriptionType.ObjectAttribute objattsamp= objattsamplist.get(0);
	//System.out.println(objattsamp.getAttribute());
	List<CodeType> codetypesamplist = langsamp.getCode();
	
	//Again returned empty
	
	DateTimeType datetimesamp = sample.getDateTime();
	CodedDescriptionType typesamp = datetimesamp.getType();
	//System.out.println(typesamp.getText());
	//The JVM is freakin out so had cancel the following lines of code
	//List<CodedDescriptionType.ObjectAttribute> objattsamplist2 = typesamp.getObjectAttribute();
	//System.out.println(objattsamplist2.isEmpty());
	//The list returned empty so skipping the following two lines
	//CodedDescriptionType.ObjectAttribute objattsamp= objattsamplist.get(0);
	//System.out.println(objattsamp.getAttribute());
	//List<CodeType> codetypesamplist2 = typesamp.getCode();
	//System.out.println(codetypesamplist2.isEmpty());


	
	MeasureType agesamp = datetimesamp.getAge();
	//agesamp is empty so following line is striked
	//System.out.println(agesamp.getValue());
	/* Not going in detail of datetimetype class because the imported xml has none of these fields recorded */
	
	List<ContinuityOfCareRecord.Patient> patientsamplist = sample.getPatient();
	ContinuityOfCareRecord.Patient patientsamp = patientsamplist.get(0);
	
	ContinuityOfCareRecord.From frmsamp = sample.getFrom();
	List<ActorReferenceType> actorlinksamplist = frmsamp.getActorLink();
	ActorReferenceType actorlinksamp = actorlinksamplist.get(0);
	
	List<CodedDescriptionType> actorrolesamplist = actorlinksamp.getActorRole();
	CodedDescriptionType actorrolesamp = actorrolesamplist.get(0);
	
	//skipping getTo() and getPurpose() as no fields in xml

	ContinuityOfCareRecord.Body bdy = sample.getBody();
	ContinuityOfCareRecord.Body.Problems prbsamp = bdy.getProblems();
	List<ProblemType> prbtypesamplist = prbsamp.getProblem();
	
	ProblemType prbtypesamp = prbtypesamplist.get(0);
	//System.out.println(prbtypesamp.getCCRDataObjectID());
	List<DateTimeType> datetimesamplist3 = prbtypesamp.getDateTime();
	DateTimeType datetimesamp3 = datetimesamplist3.get(0);
	CodedDescriptionType typesamp3 = datetimesamp3.getType();
	//System.out.println(typesamp3.getText());
	//System.out.println (datetimesamp3.getExactDateTime ());
	DateTimeType datetimesamp4 = datetimesamplist3.get(1);
	CodedDescriptionType typesamp4 = datetimesamp4.getType();
	//System.out.println(typesamp4.getText());
	//System.out.println (datetimesamp4.getExactDateTime ());
	CodedDescriptionType typesamp5 = prbtypesamp.getType();
	//System.out.println(typesamp5.getText());
	CodedDescriptionType typesamp6 = prbtypesamp.getDescription();
	con = typesamp6.getText();
        CodedDescriptionType typesamp7 = prbtypesamp.getStatus();
        stat = typesamp7.getText();
	List<CodeType> codetypesamplist2 = typesamp6.getCode();
	CodeType codetypesamp2 = codetypesamplist2.get(0);
	//System.out.println(codetypesamp2.getValue());
	//System.out.println(codetypesamp2.getCodingSystem());
	
	ContinuityOfCareRecord.Body.Medications medsamp = bdy.getMedications();
	List<StructuredProductType> medicationlist = medsamp.getMedication();
	StructuredProductType medicationsamp1 = medicationlist.get(0);
	List<StructuredProductType.Product> productsamplist = medicationsamp1.getProduct();
	StructuredProductType.Product productsamp = productsamplist.get(0);
	CodedDescriptionType typesamp8 = productsamp.getProductName();
	med = typesamp8.getText();

	EpisodesType epissamp = prbtypesamp.getEpisodes();
	//I guess no episodes recorded.
	//System.out.println(epissamp.getNumber());
	CurrentHealthStatusType currentstattypesamp = prbtypesamp.getHealthStatus();
	//no healthstatus record
	//List<DateTimeType> datetimesamplist2 = currentstattypesamp.getDateTime();
	//DateTimeType datetimesamp2 = datetimesamplist2.get(0);
	//CodedDescriptionType typesamp2 = datetimesamp2.getType();
	//System.out.println(typesamp2.getText());
	//System.out.println (datetimesamp2.getExactDateTime ());
	//CodedDescriptionType dessamp = currentstattypesamp.getDescription();
	PatientKnowledgeType patknowsamp = prbtypesamp.getPatientKnowledge();
	//No patientknowledge record also
	//System.out.println(patknowsamp.getPatientAware());
	ContinuityOfCareRecord.Body.FamilyHistory famhissamp = bdy.getFamilyHistory();
	List<FamilyHistoryType> famprbhissamplist = famhissamp.getFamilyProblemHistory();
	FamilyHistoryType famprbhissamp = famprbhissamplist.get(0);
	List<FamilyHistoryType.FamilyMember> fammemlist = famprbhissamp.getFamilyMember();
	FamilyHistoryType.FamilyMember fammem1 = fammemlist.get(0);
	CurrentHealthStatusType currentstattypesamp2 = fammem1.getHealthStatus();	


	List<FamilyHistoryType.Problem> famprblist = famprbhissamp.getProblem();
	
        //ContinuityOfCareRecord.Body.AdvanceDirectives adv = bdy.getAdvanceDirectives();
	//List<CCRCodedDataObjectType> adv1 = adv.getAdvanceDirective();
	}
	catch (JAXBException e) {
           e.printStackTrace ();
       }
   }

	public String getMedCondition(){
		return con;
	}
	public String getMedicine(){
		return med;

	}

	public String getStatus(){
		return stat;
	}
}
	
