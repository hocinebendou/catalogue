package bio.tech.catalog.web.api;

import bio.tech.catalog.forms.DatasetForm;
import bio.tech.catalog.forms.RowForm;
import bio.tech.catalog.persistence.dao.*;
import bio.tech.catalog.persistence.model.*;
import bio.tech.catalog.service.UserService;
import bio.tech.catalog.utils.StudyQuery;
import com.alibaba.fastjson.JSONObject;
import org.neo4j.ogm.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;


@RestController
@RequestMapping("/api")
public class ApiController {

	@Autowired
	private UserService userService;

	@Autowired
	private SpecimenRepository specimenRepository;

	@Autowired
    private StudyQuery studyQuery;

	@Autowired
	private StudyRepository studyRepository;

	@Autowired
	private DesignRepository designRepository;

	@Autowired
	private GenderRepository genderRepository;

	@Autowired
	private EthnicityRepository ethnicityRepository;

	@Autowired
	private ParticipantRepository participantRepository;

	@Autowired
	private CountryRepository countryRepository;

	@Autowired
	private SpecTypeRepository typeRepository;

	@Autowired
	private DatasetRepository datasetRepository;

	@Autowired
    Session session;

	@RequestMapping(value = "/studies")
	public HashMap<Long, NeoStudy> findStudies(@RequestParam(value="acronym") String acronym,
											   @RequestParam(value="design") String design) {

        Map<String, String> params = new HashMap<>();
        params.put("acronym", acronym);
        params.put("design", design);
        String query = studyQuery.constructQuery(params);
        Collection<NeoStudy> result = studyQuery.runNeoQuery(query, params);

        HashMap<Long, NeoStudy> studies = new HashMap<>();
        for (NeoStudy study: result) {
            session.load(study.getClass(), study.getId());
        	studies.put(study.getId(), study);
		}
        return studies;
	}

	@RequestMapping(value = "/user", method = RequestMethod.GET)
	public ResponseEntity<JSONObject> currentUser() {
        JSONObject obj = new JSONObject();

        User user = authenticatedUser();
		obj.put("name", user.getUsername());

        return new ResponseEntity<>(obj, HttpStatus.OK);
	}

	@RequestMapping(value = "/data", method = RequestMethod.GET)
    public ResponseEntity<JSONObject> searchAttributes() {
	    JSONObject obj = new JSONObject();

	    Collection<String> acronyms = studyRepository.findAllStudyAcronyms();
	    obj.put("acronyms", acronyms);

	    Collection<String> diseases = studyRepository.findAllStudyDiseases();
	    obj.put("diseases", diseases);

	    Collection<String> designNames = designRepository.allStudyDesignNames();
	    obj.put("designs", designNames);

	    Collection<String> sexNames = genderRepository.allGenderNames();
	    obj.put("sex", sexNames);

	    Collection<String> ethnicityNames = ethnicityRepository.findAllEthnicityNames();
	    obj.put("ethnicity", ethnicityNames);

		Collection<String> specTypeNames = typeRepository.findAllSpecimenTypeNames();
		obj.put("specTypes", specTypeNames);

		Collection<String> countries = countryRepository.findAllCountryNames();
		obj.put("countries", countries);

        return new ResponseEntity<>(obj, HttpStatus.OK);
    }

    @RequestMapping(value = "/filter", method = RequestMethod.POST)
	public ResponseEntity<JSONObject> filterStudies(@RequestBody Map<String, Object> body) {
		JSONObject obj = new JSONObject();
 		List<String> acronyms = new ArrayList<>();
		List<String> designList = new ArrayList<>();
		List<String> diseaseList = new ArrayList<>();
		List<String> sexList = new ArrayList<>();
		List<String> ethnicityList = new ArrayList<>();
		List<String> typeList = new ArrayList<>();
		List<String> countryList = new ArrayList<>();
		List<String> groupByColumns = new ArrayList<>();
		boolean acronymField = false, designField = false, diseaseField = false;
		boolean sexField = false, ethnicityField = false;
		boolean typeField = false, countryField = false;
		boolean smoking = false; boolean diet = false;
		boolean hivStatus = false; boolean bloodPressure = false; boolean alcoholUse = false;
		String bmiOp = ""; String bmiVal = "";
		String ageOp = ""; String ageVal = "";
		for (Map.Entry<String, Object> entry: body.entrySet()) {

			switch (entry.getKey()) {
				case "acronyms":
					acronyms = (List<String>) entry.getValue();
					if (acronyms.size() > 0)
						acronymField = true;
					break;
				case "designs":
					designList = (List<String>) entry.getValue();
					if (designList.size() > 0)
						designField = true;
					break;
				case "diseases":
					diseaseList = (List<String>) entry.getValue();
					if (diseaseList.size() > 0)
						diseaseField = true;
					break;
				case "sex":
					sexList = (List<String>) entry.getValue();
					if (sexList.size() > 0)
						sexField = true;
					break;
				case "ethnicity":
					ethnicityList = (List<String>) entry.getValue();
					if (ethnicityList.size() > 0)
						ethnicityField = true;
					break;
				case "specTypes":
					typeList = (List<String>) entry.getValue();
					if (typeList.size() > 0)
						typeField = true;
					break;
				case "country":
					countryList = (List<String>) entry.getValue();
					if (countryList.size() > 0)
						countryField = true;
					break;
				case "bmiOp":
					String tempBmiOp = (String) entry.getValue();
					if (!tempBmiOp.equals("Operator"))
						bmiOp = tempBmiOp;
					break;
				case "bmiVal":
					String tempBmiVal = (String) entry.getValue();
					if (tempBmiVal != null && !tempBmiVal.equals(""))
						bmiVal = tempBmiVal;
					break;
				case "ageOp":
					String tempAgeOp = (String) entry.getValue();
					if (!tempAgeOp.equals("Operator"))
						ageOp = tempAgeOp;
					break;
				case "ageVal":
					String tempAgeVal = (String) entry.getValue();
					if (tempAgeVal != null && !tempAgeVal.equals(""))
						ageVal = tempAgeVal;
					break;
				case "smoking":
					smoking = (boolean) entry.getValue();
					break;
				case "diet":
					diet = (boolean) entry.getValue();
					break;
				case "hivStatus":
					hivStatus = (boolean) entry.getValue();
					break;
				case "bloodPressure":
					bloodPressure = (boolean) entry.getValue();
					break;
				case "alcoholUse":
					alcoholUse = (boolean) entry.getValue();
					break;
				case "columns":
					groupByColumns = (List<String>) entry.getValue();
					break;
			}
		}

		// find study acronyms
		List<String> acronymList = new ArrayList<>();
		boolean findByAcronyms = false;
		if (smoking) {
			if (acronymField)
				acronyms = studyRepository.findStudiesByAcronymsAndAttribute(acronyms, "smoking");
			else
				acronyms = studyRepository.findStudiesByAttribute("smoking");
			findByAcronyms = true;
		}

		if (diet) {
			if (acronymField || findByAcronyms)
				acronyms = studyRepository.findStudiesByAcronymsAndAttribute(acronyms, "diet");
			else
				acronyms = studyRepository.findStudiesByAttribute("diet");
			findByAcronyms = true;
		}

		if (hivStatus) {
			if (acronymField || findByAcronyms)
				acronyms = studyRepository.findStudiesByAcronymsAndAttribute(acronyms, "HIV status");
			else
				acronyms = studyRepository.findStudiesByAttribute("HIV status");
			findByAcronyms = true;
		}

		if (bloodPressure) {
			if (acronymField || findByAcronyms)
				acronyms = studyRepository.findStudiesByAcronymsAndAttribute(acronyms, "blood_pressure");
			else
				acronyms = studyRepository.findStudiesByAttribute("blood_pressure");
			findByAcronyms = true;
		}

		if (alcoholUse) {
			if (acronymField || findByAcronyms)
				acronyms = studyRepository.findStudiesByAcronymsAndAttribute(acronyms, "alcohol_use");
			else
				acronyms = studyRepository.findStudiesByAttribute("alcohol_use");
		}

		if (!acronyms.isEmpty() && ! acronymField)
			acronymField = true;

		if (acronymField && diseaseField && designField) {
			acronymList = studyRepository.findByAcronymsAndDiseasesAndDesigns(acronyms, diseaseList, designList);
		} else if (acronymField && diseaseField) {
			acronymList = studyRepository.findByAcronymsAndDiseases(acronyms, diseaseList);
		} else if (acronymField && designField) {
			acronymList = studyRepository.findByAcronymsAndDesigns(acronyms, designList);
		} else if (diseaseField && designField) {
			acronymList = studyRepository.findByDiseasesAndDesigns(diseaseList, designList);
		} else if (diseaseField) {
			acronymList = studyRepository.findByDiseases(diseaseList);
		} else if (designField) {
			acronymList = studyRepository.findByDesigns(designList);
		} else if (acronymField) {
			acronymList = acronyms;
		}

		// find participant ids
		List<String> participantIds = new ArrayList<>();
		if (!acronymList.isEmpty() && sexField && ethnicityField) {
			participantIds = participantRepository.findByAcronymsAndSexAndEthnicity(acronymList,
					sexList, ethnicityList);
		} else if (!acronymList.isEmpty() && sexField) {
			participantIds = participantRepository.findByAcronymsAndSex(acronymList, sexList);
		} else if (!acronymList.isEmpty() && ethnicityField) {
			participantIds = participantRepository.findByAcronymsAndEthnicity(acronymList, ethnicityList);
		} else if (sexField && ethnicityField) {
			participantIds = participantRepository.findBySexAndEthnicity(sexList, ethnicityList);
		} else if (sexField) {
			participantIds = participantRepository.findBySex(sexList);
		} else if (ethnicityField) {
			participantIds = participantRepository.findByEthnicity(ethnicityList);
		}

		if (!participantIds.isEmpty() && !bmiOp.equals("") && !bmiVal.equals("")) {
			switch (bmiOp){
				case "=":
					participantIds = participantRepository.findParticipantsByIdsAndEqualsToBmi(participantIds, bmiVal);
					break;
				case ">":
					participantIds = participantRepository.findParticipantsByIdsAndGreaterThanBmi(participantIds, bmiVal);
					break;
				case "<":
					participantIds = participantRepository.findParticipantsByIdsAndLessThanBmi(participantIds, bmiVal);
					break;
			}
		} else if (!bmiOp.equals("") && !bmiVal.equals("")) {
			switch (bmiOp) {
				case "=":
					participantIds = participantRepository.findParticipantsByEqualsToBmi(bmiVal);
					break;
				case ">":
					participantIds = participantRepository.findParticipantsByGreaterThanBmi(bmiVal);
					break;
				case "<":
					participantIds = participantRepository.findParticipantsByLessThanBmi(bmiVal);
					break;
			}
		}

		boolean findByIds = false;
		if (!bmiOp.equals("") && !bmiVal.equals(""))
			findByIds = true;

		if (!participantIds.isEmpty() && !ageOp.equals("") && !ageVal.equals("")) {
			switch (ageOp){
				case "=":
					participantIds = participantRepository.findParticipantsByIdsAndEqualsToAge(participantIds, ageVal);
					break;
				case ">":
					participantIds = participantRepository.findParticipantsByIdsAndGreaterThanAge(participantIds, ageVal);
					break;
				case "<":
					participantIds = participantRepository.findParticipantsByIdsAndLessThanAge(participantIds, ageVal);
					break;
			}
		} else if (!ageOp.equals("") && !ageVal.equals("") && !findByIds) {
			switch (ageOp) {
				case "=":
					participantIds = participantRepository.findParticipantsByEqualsToAge(ageVal);
					break;
				case ">":
					participantIds = participantRepository.findParticipantsByGreaterThanAge(ageVal);
					break;
				case "<":
					participantIds = participantRepository.findParticipantsByLessThanAge(ageVal);
					break;
			}
		}

		// find biospecimens
		List<NeoSpecimen> specimens = new ArrayList<>();
		if (!acronymList.isEmpty() && !participantIds.isEmpty() && typeField && countryField) {
			specimens = specimenRepository.findByAcronymsAndParticipantIdsAndTypesAndCountries(acronymList,
					participantIds, typeList, countryList);
		} else if (!acronymList.isEmpty() && !participantIds.isEmpty() && typeField) {
			specimens = specimenRepository.findByAcronymsAndParticipantIdsAndTypes(acronymList,
					participantIds, typeList);
		} else if (!acronymList.isEmpty() && !participantIds.isEmpty() && countryField) {
			specimens = specimenRepository.findByAcronymsAndParticipantIdsAndCountries(acronymList,
					participantIds, countryList);
		} else if (!acronymList.isEmpty() && !participantIds.isEmpty()) {
			specimens = specimenRepository.findByAcronymsAndParticipantIds(acronymList, participantIds);
		} else if (!acronymList.isEmpty() && typeField) {
			specimens = specimenRepository.findByAcronymsAndTypes(acronymList, typeList);
		} else if (!acronymList.isEmpty() && countryField) {
			specimens = specimenRepository.findByAcronymsAndCountries(acronymList, countryList);
		} else if (!participantIds.isEmpty() && typeField) {
			specimens = specimenRepository.findByParticipantIdsAndTypes(participantIds, typeList);
		} else if (!participantIds.isEmpty() && countryField) {
			specimens = specimenRepository.findByParticipantIdsAndCountries(participantIds, countryList);
		} else if (typeField && countryField) {
			specimens = specimenRepository.findByTypesAndCountries(typeList, countryList);
		} else if (!acronymList.isEmpty()) {
			specimens = specimenRepository.findByAcronyms(acronymList);
		} else if (!participantIds.isEmpty()) {
			specimens = specimenRepository.findByParticipantIds(participantIds);
		} else if (typeField) {
			specimens = specimenRepository.findBySpecimenTypes(typeList);
		} else if (countryField) {
			specimens = specimenRepository.findByCountries(countryList);
		}

		List<String> allCountries = countryRepository.findAllCountryNames();
		List<String> allEthnicities = ethnicityRepository.findAllEthnicityNames();
		List<String> allSpecimenTypes = typeRepository.findAllSpecimenTypeNames();
		if (acronymList.isEmpty())
			acronymList = studyRepository.findAllStudyAcronyms();

		List<RowForm> groupedSpecimens;

		// 4
		if ( groupByColumns.contains("Disease") && groupByColumns.contains("Ethnicity") && groupByColumns.contains("SpecType") && groupByColumns.contains("Country"))
			groupedSpecimens = groupByTypeAndEthnicityAndCountryAndDisease(specimens, acronymList, allSpecimenTypes, allEthnicities, allCountries, diseaseList);

		else if (groupByColumns.contains("Design") && groupByColumns.contains("Disease") && groupByColumns.contains("Sex") && groupByColumns.contains("Ethnicity"))
			groupedSpecimens = groupByDesignAndDiseaseAndSexAndEthnicity(specimens, acronymList, designList, diseaseList, sexList, allEthnicities);

		else if (groupByColumns.contains("Design") && groupByColumns.contains("Disease") && groupByColumns.contains("Sex") && groupByColumns.contains("SpecType"))
			groupedSpecimens = groupByDesignAndDiseaseAndSexAndType(specimens, acronymList, designList, diseaseList, sexList, allSpecimenTypes);

		else if (groupByColumns.contains("Design") && groupByColumns.contains("Disease") && groupByColumns.contains("Sex") && groupByColumns.contains("Country"))
			groupedSpecimens = groupByDesignAndDiseaseAndSexAndCountry(specimens, acronymList, designList, diseaseList, sexList, allCountries);

		// 3
		else if (groupByColumns.contains("SpecType") && groupByColumns.contains("Ethnicity") && groupByColumns.contains("Country"))
			groupedSpecimens = groupByTypeAndEthnicityAndCountry(specimens, acronymList, allSpecimenTypes, allEthnicities, allCountries);

		else if (groupByColumns.contains("Ethnicity") && groupByColumns.contains("Country") && groupByColumns.contains("Disease"))
			groupedSpecimens = groupByEthnicityAndCountryAndDisease(specimens, acronymList, allEthnicities, allCountries, diseaseList);

		else if (groupByColumns.contains("SpecType") && groupByColumns.contains("Country") && groupByColumns.contains("Disease"))
			groupedSpecimens = groupByTypeAndCountryAndDisease(specimens, acronymList, allSpecimenTypes, allCountries, diseaseList);

		else if (groupByColumns.contains("SpecType") && groupByColumns.contains("Ethnicity") && groupByColumns.contains("Disease"))
			groupedSpecimens = groupByTypeAndEthnicityAndDisease(specimens, acronymList, allSpecimenTypes, allEthnicities, diseaseList);

		else if (groupByColumns.contains("Design") && groupByColumns.contains("Ethnicity") && groupByColumns.contains("SpecType"))
			groupedSpecimens = groupByDesignAndEthnicityAndType(specimens, acronymList, designList, allEthnicities, allSpecimenTypes);

		else if (groupByColumns.contains("Design") && groupByColumns.contains("Sex") &&	groupByColumns.contains("SpecType"))
			groupedSpecimens = groupByDesignAndSexAndType(specimens, acronymList, designList, sexList, allSpecimenTypes);

		else if (groupByColumns.contains("Design") && groupByColumns.contains("Disease") && groupByColumns.contains("Sex"))
			groupedSpecimens = groupByDesignAndSexAndDisease(specimens, acronymList, designList, sexList, diseaseList);

		else if (groupByColumns.contains("Design") && groupByColumns.contains("Sex") && groupByColumns.contains("Ethnicity"))
			groupedSpecimens = groupByDesignAndSexAndEthnicity(specimens, acronymList, designList, sexList, allEthnicities);

		else if (groupByColumns.contains("Disease") && groupByColumns.contains("Sex") && groupByColumns.contains("Ethnicity"))
			groupedSpecimens = groupByDiseaseAndSexAndEthnicity(specimens, acronymList, diseaseList, sexList, allEthnicities);

		// 2
		else if (groupByColumns.contains("Disease") && 	groupByColumns.contains("Sex"))
			groupedSpecimens = groupByDiseaseAndSex(specimens, acronymList, diseaseList, sexList);

		else if (groupByColumns.contains("Sex") && 	groupByColumns.contains("Ethnicity"))
			groupedSpecimens = groupBySexAndEthnicity(specimens, acronymList, sexList, allEthnicities);

		else if (groupByColumns.contains("SpecType") && groupByColumns.contains("Ethnicity"))
			groupedSpecimens = groupByTypeAndEthnicity(specimens, acronymList, allSpecimenTypes, allEthnicities);

		else if (groupByColumns.contains("SpecType") && groupByColumns.contains("Country"))
			groupedSpecimens = groupByTypeAndCountry(specimens, acronymList, allSpecimenTypes, allCountries);

		else if (groupByColumns.contains("SpecType") && groupByColumns.contains("Disease"))
			groupedSpecimens = groupByTypeAndDisease(specimens, acronymList, allSpecimenTypes, diseaseList);

		else if (groupByColumns.contains("Ethnicity") && groupByColumns.contains("Country"))
			groupedSpecimens = groupByEthnicityAndCountry(specimens, acronymList, allEthnicities, allCountries);

		else if (groupByColumns.contains("Ethnicity") && groupByColumns.contains("Disease"))
			groupedSpecimens = groupByEthnicityAndDisease(specimens, acronymList, allEthnicities, diseaseList);

		else if (groupByColumns.contains("Country") && groupByColumns.contains("Disease"))
			groupedSpecimens = groupByCountryAndDisease(specimens, acronymList, allCountries, diseaseList);

		else if (groupByColumns.contains("Design") && groupByColumns.contains("Disease"))
			groupedSpecimens = groupByDesignAndDisease(specimens, acronymList, designList, diseaseList);

		else if (groupByColumns.contains("SpecType") && groupByColumns.contains("Design"))
			groupedSpecimens = groupByDesignAndType(specimens, acronymList, allSpecimenTypes, designList);

		else if (groupByColumns.contains("Design") && groupByColumns.contains("Sex"))
			groupedSpecimens = groupByDesignAndSex(specimens, acronymList, designList, sexList);

		// 1
		else if (groupByColumns.contains("SpecType"))
			groupedSpecimens = groupByType(specimens, acronymList, allSpecimenTypes);

		else if (groupByColumns.contains("Ethnicity"))
			groupedSpecimens = groupByEthnicity(specimens, acronymList, allEthnicities);

		else if (groupByColumns.contains("Country"))
			groupedSpecimens = groupByCountry(specimens, acronymList, allCountries);

		else if (groupByColumns.contains("Disease"))
			groupedSpecimens = groupByDisease(specimens, acronymList, diseaseList);

		else if (groupByColumns.contains("Design"))
			groupedSpecimens = groupByDesign(specimens, acronymList, designList);

		else if (groupByColumns.contains("Sex"))
			groupedSpecimens = groupBySex(specimens, acronymList, sexList);

		else
			groupedSpecimens = groupByAcronym(specimens, acronymList);

		// find datasets
		List<DatasetForm> datasets = datasetStudies(groupedSpecimens);

		obj.put("specimens", groupedSpecimens);
		obj.put("datasets", datasets);

		return new ResponseEntity<>(obj, HttpStatus.OK);
	}

	/*
	 * NON API
	 */

	private User authenticatedUser() {
		final org.springframework.security.core.userdetails.User authenticated = (org.springframework.security.core.userdetails.User)
				SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		return userService.findUserByEmail(authenticated.getUsername());
	}

	private List<DatasetForm> datasetStudies(List<RowForm> specimens) {
		List<DatasetForm> datasets = new ArrayList<>();
		Set<String> egacodes = new HashSet<>();
		for (RowForm specimen : specimens) {
			String acronym = specimen.getAcronym();
			List<NeoDataSet> sets = datasetRepository.findAllByStudyAcronym(acronym);
			for (NeoDataSet set : sets) {
				if (egacodes.contains(set.getEgaAccess())) continue;

				egacodes.add(set.getEgaAccess());
				DatasetForm dataset = new DatasetForm();
				dataset.setAcronym(acronym);
				dataset.setEgaAccess(set.getEgaAccess());
				datasets.add(dataset);
			}
		}

		return datasets;
	}

	private List<RowForm> groupByTypeAndEthnicityAndCountryAndDisease(List<NeoSpecimen> specimens,
																		 List<String> acronyms,
																		 List<String> specimenTypes,
																		 List<String> ethnicities,
																		 List<String> countries,
																		 List<String> diseases) {
		List<RowForm> groupedSpecimens = new ArrayList<>();
		for (String acronym : acronyms)
			for (String type : specimenTypes)
				for (String ethnicity : ethnicities)
					for (String country : countries)
						for (String disease : diseases) {
							JSONObject object = new JSONObject();
							RowForm row = new RowForm();
							row.setAcronym(acronym);
							row.setSpecType(type);
							row.setEthnicity(ethnicity);
							row.setCountry(country);
							row.setDisease(disease);
							row.setNbSamples(0);
							HashSet<String> biobanks = new HashSet<>();
							int nbSamples = 0;
							for (NeoSpecimen specimen : specimens)
								if (specimen.getAcronym().equals(acronym) &&
										specimen.getSpecType().getName().equals(type) &&
										specimen.getEthnicity().equals(ethnicity) &&
										specimen.getCountry().getName().equals(country) &&
										specimen.getDisease().equals(disease)) {
									nbSamples += 1;
									if (specimen.getNoAliquots() > 0)
										biobanks.add(specimen.getBiobankName());
								}
							if (nbSamples > 0) {
								row.setBiobanks(new ArrayList<>(biobanks));
								row.setNbSamples(nbSamples);
								groupedSpecimens.add(row);
							}
						}

		return groupedSpecimens;
	}

	private List<RowForm> groupByTypeAndEthnicityAndCountry(List<NeoSpecimen> specimens,
															   List<String> acronyms,
															   List<String> specimenTypes,
															   List<String> ethnicities,
															   List<String> countries) {
		List<RowForm> groupedSpecimens = new ArrayList<>();
		for (String acronym : acronyms)
			for (String type : specimenTypes)
				for (String ethnicity : ethnicities)
					for (String country : countries) {
						RowForm row = new RowForm();
						row.setAcronym(acronym);
						row.setSpecType(type);
						row.setEthnicity(ethnicity);
						row.setCountry(country);
						HashSet<String> biobanks = new HashSet<>();
						int nbSamples = 0;
						for (NeoSpecimen specimen : specimens)
							if (specimen.getAcronym().equals(acronym) &&
									specimen.getSpecType().getName().equals(type) &&
									specimen.getEthnicity().equals(ethnicity) &&
									specimen.getCountry().getName().equals(country)) {
								nbSamples += 1;
								if (specimen.getNoAliquots() > 0)
									biobanks.add(specimen.getBiobankName());
							}
						if (nbSamples > 0) {
							row.setNbSamples(nbSamples);
							row.setBiobanks(new ArrayList<>(biobanks));
							groupedSpecimens.add(row);
						}
					}

		return groupedSpecimens;
	}

	private List<RowForm> groupByTypeAndCountryAndDisease(List<NeoSpecimen> specimens,
															 List<String> acronyms,
															 List<String> specimenTypes,
															 List<String> countries,
															 List<String> diseases) {
		List<RowForm> groupedSpecimens = new ArrayList<>();
		for (String acronym : acronyms)
			for (String type : specimenTypes)
				for (String country : countries)
					for (String disease : diseases) {
						RowForm row = new RowForm();
						row.setAcronym(acronym);
						row.setSpecType(type);
						row.setCountry(country);
						row.setDisease(disease);
						HashSet<String> biobanks = new HashSet<>();
						int nbSamples = 0;
						for (NeoSpecimen specimen : specimens)
							if (specimen.getAcronym().equals(acronym) &&
									specimen.getSpecType().getName().equals(type) &&
									specimen.getCountry().getName().equals(country) &&
									specimen.getDisease().equals(disease)) {

								nbSamples += 1;
								if (specimen.getNoAliquots() > 0)
									biobanks.add(specimen.getBiobankName());
							}
						if (nbSamples > 0) {
							row.setNbSamples(nbSamples);
							row.setBiobanks(new ArrayList<>(biobanks));
							groupedSpecimens.add(row);
						}
					}

		return groupedSpecimens;
	}

	private List<RowForm> groupByTypeAndEthnicityAndDisease(List<NeoSpecimen> specimens,
															   List<String> acronyms,
															   List<String> specimenTypes,
															   List<String> ethnicities,
															   List<String> diseases) {
		List<RowForm> groupedSpecimens = new ArrayList<>();
		for (String acronym : acronyms)
			for (String type : specimenTypes) {
				NeoSpecType specType = typeRepository.findNeoSpecTypeByName(type);
				for (String ethnicity : ethnicities)
					for (String disease : diseases) {
						RowForm row = new RowForm();
						row.setAcronym(acronym);
						row.setSpecType(type);
						row.setEthnicity(ethnicity);
						row.setDisease(disease);
						HashSet<String> biobanks = new HashSet<>();
						int nbSamples = 0;
						for (NeoSpecimen specimen : specimens)
							if (specimen.getAcronym().equals(acronym) &&
									specimen.getSpecType().getName().equals(specType.getName()) &&
									specimen.getEthnicity().equals(ethnicity) &&
									specimen.getDisease().equals(disease)) {

								nbSamples += 1;
								if (specimen.getNoAliquots() > 0)
									biobanks.add(specimen.getBiobankName());
							}
						if (nbSamples > 0) {
							row.setNbSamples(nbSamples);
							row.setBiobanks(new ArrayList<>(biobanks));
							groupedSpecimens.add(row);
						}
					}
			}
		return groupedSpecimens;
	}

	private List<RowForm> groupByTypeAndEthnicity(List<NeoSpecimen> specimens,
													  List<String> acronyms,
													  List<String> specimenTypes,
													  List<String> ethnicities) {
		List<RowForm> groupedSpecimens = new ArrayList<>();
		for (String acronym : acronyms)
			for (String type : specimenTypes) {
				NeoSpecType specType = typeRepository.findNeoSpecTypeByName(type);
				for (String ethnicity : ethnicities) {
					RowForm row = new RowForm();
					row.setAcronym(acronym);
					row.setSpecType(type);
					row.setEthnicity(ethnicity);
					HashSet<String> biobanks = new HashSet<>();
					int nbSamples = 0;
					for (NeoSpecimen specimen : specimens)
						if (specimen.getAcronym().equals(acronym) &&
								specimen.getSpecType().getName().equals(specType.getName()) &&
								specimen.getEthnicity().equals(ethnicity)) {

							nbSamples += 1;
							if (specimen.getNoAliquots() > 0)
								biobanks.add(specimen.getBiobankName());
						}
					if (nbSamples > 0) {
						row.setNbSamples(nbSamples);
						row.setBiobanks(new ArrayList<>(biobanks));
						groupedSpecimens.add(row);
					}
				}
			}

		return groupedSpecimens;
	}

	private List<RowForm> groupByTypeAndCountry(List<NeoSpecimen> specimens,
												    List<String> acronyms,
												    List<String> specimenTypes,
												    List<String> countries) {
		List<RowForm> groupedSpecimens = new ArrayList<>();
		for (String acronym : acronyms)
			for (String type : specimenTypes) {
				NeoSpecType specType = typeRepository.findNeoSpecTypeByName(type);
				for (String country : countries) {
					RowForm row = new RowForm();
					row.setAcronym(acronym);
					row.setSpecType(type);
					row.setCountry(country);
					HashSet<String> biobanks = new HashSet<>();
					int nbSamples = 0;
					for (NeoSpecimen specimen : specimens)
						if (specimen.getAcronym().equals(acronym) &&
								specimen.getSpecType().getName().equals(specType.getName()) &&
								specimen.getCountry().getName().equals(country)) {

							nbSamples += 1;
							if (specimen.getNoAliquots() > 0)
								biobanks.add(specimen.getBiobankName());
						}
					if (nbSamples > 0) {
						row.setNbSamples(nbSamples);
						row.setBiobanks(new ArrayList<>(biobanks));
						groupedSpecimens.add(row);
					}
				}
			}
		return groupedSpecimens;
	}

	private List<RowForm> groupByTypeAndDisease(List<NeoSpecimen> specimens,
													List<String> acronyms,
													List<String> specimenTypes,
													List<String> diseases) {
		List<RowForm> groupedSpecimens = new ArrayList<>();
		for (String acronym : acronyms)
			for (String type : specimenTypes) {
				NeoSpecType specType = typeRepository.findNeoSpecTypeByName(type);
				for (String disease : diseases) {
					JSONObject object = new JSONObject();
					RowForm row = new RowForm();
					row.setAcronym(acronym);
					row.setSpecType(type);
					row.setDisease(disease);
					HashSet<String> biobanks = new HashSet<>();
					int nbSamples = 0;
					for (NeoSpecimen specimen : specimens)
						if (specimen.getAcronym().equals(acronym) &&
								specimen.getSpecType().getName().equals(specType.getName()) &&
								specimen.getDisease().equals(disease)) {

							nbSamples += 1;
							if (specimen.getNoAliquots() > 0)
								biobanks.add(specimen.getBiobankName());
						}
					if (nbSamples > 0) {
						row.setNbSamples(nbSamples);
						row.setBiobanks(new ArrayList<>(biobanks));
						groupedSpecimens.add(row);
					}
				}
			}
		return groupedSpecimens;
	}

	private List<RowForm> groupByType(List<NeoSpecimen> specimens,
										 List<String> acronyms,
										 List<String> specimenTypes) {
		List<RowForm> groupedSpecimens = new ArrayList<>();
		for (String acronym : acronyms)
			for (String type : specimenTypes) {
				RowForm row = new RowForm();
				row.setAcronym(acronym);
				row.setSpecType(type);
				NeoSpecType specType = typeRepository.findNeoSpecTypeByName(type);
				HashSet<String> biobanks = new HashSet<>();
				int nbSamples = 0;
				for (NeoSpecimen specimen : specimens)
					if (specimen.getAcronym().equals(acronym) &&
							specimen.getSpecType().getName().equals(specType.getName())) {

						nbSamples += 1;
						if (specimen.getNoAliquots() > 0)
							biobanks.add(specimen.getBiobankName());
					}
				if (nbSamples > 0) {
					row.setNbSamples(nbSamples);
					row.setBiobanks(new ArrayList<>(biobanks));
					groupedSpecimens.add(row);
				}
			}

		return groupedSpecimens;
	}

	private List<RowForm> groupByEthnicityAndCountryAndDisease(List<NeoSpecimen> specimens,
																  List<String> acronyms,
																  List<String> ethnicities,
																  List<String> countries,
																  List<String> diseases) {
		List<RowForm> groupedSpecimens = new ArrayList<>();
		for (String acronym : acronyms)
			for (String ethnicity : ethnicities)
				for (String country : countries)
					for (String disease : diseases) {
						RowForm row = new RowForm();
						row.setAcronym(acronym);
						row.setEthnicity(ethnicity);
						row.setCountry(country);
						row.setDisease(disease);
						HashSet<String> biobanks = new HashSet<>();
						int nbSamples = 0;
						for (NeoSpecimen specimen : specimens)
							if (specimen.getAcronym().equals(acronym) &&
									specimen.getEthnicity().equals(ethnicity) &&
									specimen.getCountry().getName().equals(country) &&
									specimen.getDisease().equals(disease)) {

								nbSamples += 1;
								if (specimen.getNoAliquots() > 0)
									biobanks.add(specimen.getBiobankName());
							}
						if (nbSamples > 0) {
							row.setNbSamples(nbSamples);
							row.setBiobanks(new ArrayList<>(biobanks));
							groupedSpecimens.add(row);
						}
					}

		return groupedSpecimens;
	}


	private List<RowForm> groupByEthnicityAndCountry(List<NeoSpecimen> specimens,
														List<String> acronyms,
														List<String> ethnicities,
														List<String> countries) {
		List<RowForm> groupedSpecimens = new ArrayList<>();
		for (String acronym : acronyms)
			for (String ethnicity : ethnicities)
				for (String country : countries) {
					RowForm row = new RowForm();
					row.setAcronym(acronym);
					row.setEthnicity(ethnicity);
					row.setCountry(country);
					HashSet<String> biobanks = new HashSet<>();
					int nbSamples = 0;
					for (NeoSpecimen specimen : specimens)
						if (specimen.getAcronym().equals(acronym) &&
								specimen.getEthnicity().equals(ethnicity) &&
								specimen.getCountry().getName().equals(country)) {

							nbSamples += 1;
							if (specimen.getNoAliquots() > 0)
								biobanks.add(specimen.getBiobankName());
						}
					if (nbSamples > 0) {
						row.setNbSamples(nbSamples);
						row.setBiobanks(new ArrayList<>(biobanks));
						groupedSpecimens.add(row);
					}
				}

		return groupedSpecimens;
	}

	private List<RowForm> groupByCountryAndDisease(List<NeoSpecimen> specimens,
													  List<String> acronyms,
													  List<String> countries,
													  List<String> diseases) {
		List<RowForm> groupedSpecimens = new ArrayList<>();
		for (String acronym : acronyms)
			for (String country : countries)
				for (String disease : diseases) {
					RowForm row = new RowForm();
					row.setAcronym(acronym);
					row.setCountry(country);
					row.setDisease(disease);
					HashSet<String> biobanks = new HashSet<>();
					int nbSamples = 0;
					for (NeoSpecimen specimen : specimens)
						if (specimen.getAcronym().equals(acronym) &&
								specimen.getCountry().getName().equals(country) &&
								specimen.getDisease().equals(disease)) {
							nbSamples += 1;
							if (specimen.getNoAliquots() > 0)
								biobanks.add(specimen.getBiobankName());
						}
					if (nbSamples > 0) {
						row.setNbSamples(nbSamples);
						row.setBiobanks(new ArrayList<>(biobanks));
						groupedSpecimens.add(row);
					}
				}

		return groupedSpecimens;
	}

	private List<RowForm> groupByEthnicityAndDisease(List<NeoSpecimen> specimens,
														List<String> acronyms,
														List<String> ethnicities,
														List<String> diseases) {
		List<RowForm> groupedSpecimens = new ArrayList<>();
		for (String acronym : acronyms) {
			for (String ethnicity : ethnicities)
				for (String disease : diseases) {
					RowForm row = new RowForm();
					row.setAcronym(acronym);
					row.setEthnicity(ethnicity);
					row.setDisease(disease);
					HashSet<String> biobanks = new HashSet<>();
					int nbSamples = 0;
					for (NeoSpecimen specimen : specimens)
						if (specimen.getAcronym().equals(acronym) &&
								specimen.getEthnicity().equals(ethnicity) &&
								specimen.getDisease().equals(disease)) {

							nbSamples += 1;
							if (specimen.getNoAliquots() > 0)
								biobanks.add(specimen.getBiobankName());
						}
					if (nbSamples > 0) {
						row.setNbSamples(nbSamples);
						row.setBiobanks(new ArrayList<>(biobanks));
						groupedSpecimens.add(row);
					}
				}
		}

		return groupedSpecimens;
	}

	private HashMap<String, NeoStudy> studyHashMap() {
		HashMap<String, NeoStudy> studyMap = new HashMap<>();
		List<NeoStudy> studies = studyRepository.findAll();
		for (NeoStudy study : studies)
			studyMap.put(study.getAcronym(), study);

		return studyMap;
	}

	private List<RowForm> groupByDesignAndType(List<NeoSpecimen> specimens,
											   List<String> acronyms,
											   List<String> specimenTypes,
											   List<String> designList) {

		List<RowForm> groupedSpecimens = new ArrayList<>();
		HashMap<String, NeoStudy> studies = studyHashMap();
		for (String acronym : acronyms) {
			for (String design : designList) {
				NeoStudy study = studies.get(acronym);
				List<String> studyDesigns = new ArrayList<>();
				study.getDesigns().forEach(item -> studyDesigns.add(item.getName()));
				for (String type : specimenTypes ) {
					NeoSpecType specType = typeRepository.findNeoSpecTypeByName(type);
					RowForm row = new RowForm();
					row.setAcronym(acronym);
					row.setDesign(design);
					row.setSpecType(type);

					HashSet<String> biobanks = new HashSet<>();
					int nbSamples = 0;
					for (NeoSpecimen specimen : specimens)
						if (specimen.getAcronym().equals(acronym) &&
								studyDesigns.contains(design) &&
								specimen.getSpecType().getName().equals(specType.getName())) {

							nbSamples += 1;
							if (specimen.getNoAliquots() > 0)
								biobanks.add(specimen.getBiobankName());
						}
					if (nbSamples > 0) {
						row.setNbSamples(nbSamples);
						row.setBiobanks(new ArrayList<>(biobanks));
						groupedSpecimens.add(row);
					}
				}
			}
		}
		return groupedSpecimens;

	}
	private List<RowForm> groupByDesignAndEthnicityAndType(List<NeoSpecimen> specimens,
														   List<String> acronyms,
														   List<String> designs,
														   List<String> ethnicities,
														   List<String> specimenTypes) {
		List<RowForm> groupedSpecimens = new ArrayList<>();
		HashMap<String, NeoStudy> studies = studyHashMap();
		for (String acronym : acronyms) {
			for (String design : designs) {
				NeoStudy study = studies.get(acronym);
				List<String> studyDesigns = new ArrayList<>();
				study.getDesigns().forEach(item -> studyDesigns.add(item.getName()));
				for (String ethnicity: ethnicities)
					for (String type : specimenTypes) {
						NeoSpecType specType = typeRepository.findNeoSpecTypeByName(type);
						RowForm row = new RowForm();
						row.setAcronym(acronym);
						row.setDesign(design);
						row.setEthnicity(ethnicity);
						row.setSpecType(type);
						HashSet<String> biobanks = new HashSet<>();
						int nbSamples = 0;
						for (NeoSpecimen specimen : specimens)
							if (specimen.getAcronym().equals(acronym) &&
									studyDesigns.contains(design) &&
									specimen.getEthnicity().equals(ethnicity) &&
									specimen.getSpecType().getName().equals(specType.getName())) {

								nbSamples += 1;
								if (specimen.getNoAliquots() > 0)
									biobanks.add(specimen.getBiobankName());
							}
						if (nbSamples > 0) {
							row.setNbSamples(nbSamples);
							row.setBiobanks(new ArrayList<>(biobanks));
							groupedSpecimens.add(row);
						}
					}
			}
		}

		return groupedSpecimens;
	}

	private List<RowForm> groupBySexAndEthnicity(List<NeoSpecimen> specimens,
														  List<String> acronyms,
														  List<String> sexList,
														  List<String> ethnicities) {
		List<RowForm> groupedSpecimens = new ArrayList<>();
		for (String acronym : acronyms) {
			for (String sex: sexList)
				for (String ethinicity : ethnicities) {
					RowForm row = new RowForm();
					row.setAcronym(acronym);
					row.setSex(sex);
					row.setEthnicity(ethinicity);
					HashSet<String> biobanks = new HashSet<>();
					int nbSamples = 0;
					for (NeoSpecimen specimen : specimens)
						if (specimen.getAcronym().equals(acronym) &&
								specimen.getSex().equals(sex) &&
								specimen.getEthnicity().equals(ethinicity)) {

							nbSamples += 1;
							if (specimen.getNoAliquots() > 0)
								biobanks.add(specimen.getBiobankName());
						}
					if (nbSamples > 0) {
						row.setNbSamples(nbSamples);
						row.setBiobanks(new ArrayList<>(biobanks));
						groupedSpecimens.add(row);
					}
				}
		}

		return groupedSpecimens;
	}

	private List<RowForm> groupByDiseaseAndSex(List<NeoSpecimen> specimens,
											   List<String> acronyms,
											   List<String> diseases,
											   List<String> sexList) {
		List<RowForm> groupedSpecimens = new ArrayList<>();
		for (String acronym : acronyms) {
			for (String disease : diseases)
				for (String sex : sexList) {
					RowForm row = new RowForm();
					row.setAcronym(acronym);
					row.setDisease(disease);
					row.setSex(sex);
					HashSet<String> biobanks = new HashSet<>();
					int nbSamples = 0;
					for (NeoSpecimen specimen : specimens)
						if (specimen.getAcronym().equals(acronym) &&
								specimen.getDisease().equals(disease) &&
								specimen.getSex().equals(sex)) {

							nbSamples += 1;
							if (specimen.getNoAliquots() > 0)
								biobanks.add(specimen.getBiobankName());
						}
					if (nbSamples > 0) {
						row.setNbSamples(nbSamples);
						row.setBiobanks(new ArrayList<>(biobanks));
						groupedSpecimens.add(row);
					}
				}
		}

		return groupedSpecimens;
	}

	private List<RowForm> groupByDesignAndDiseaseAndSexAndEthnicity(List<NeoSpecimen> specimens,
																	List<String> acronyms,
																	List<String> designs,
																	List<String> diseases,
																	List<String> sexList,
																	List<String> ethnicities) {
		List<RowForm> groupedSpecimens = new ArrayList<>();
		HashMap<String, NeoStudy> studies = studyHashMap();
		for (String acronym : acronyms) {
			for (String design : designs) {
				NeoStudy study = studies.get(acronym);
				List<String> studyDesigns = new ArrayList<>();
				study.getDesigns().forEach(item -> studyDesigns.add(item.getName()));
				for (String disease : diseases) {
					for (String sex: sexList) {
						for (String ethinicity : ethnicities) {
							RowForm row = new RowForm();
							row.setAcronym(acronym);
							row.setDesign(design);
							row.setDisease(disease);
							row.setSex(sex);
							row.setEthnicity(ethinicity);
							HashSet<String> biobanks = new HashSet<>();
							int nbSamples = 0;
							for (NeoSpecimen specimen : specimens) {
								if (specimen.getAcronym().equals(acronym) &&
										studyDesigns.contains(design) &&
										specimen.getDisease().equals(disease) &&
										specimen.getSex().equals(sex) &&
										specimen.getEthnicity().equals(ethinicity)) {
									nbSamples += 1;
									if (specimen.getNoAliquots() > 0)
										biobanks.add(specimen.getBiobankName());
								}
							}
							if (nbSamples > 0) {
								row.setNbSamples(nbSamples);
								row.setBiobanks(new ArrayList<>(biobanks));
								groupedSpecimens.add(row);
							}
						}
					}
				}
			}
		}
		return groupedSpecimens;
	}

	private List<RowForm> groupByDesignAndSexAndEthnicity(List<NeoSpecimen> specimens,
													 List<String> acronyms,
													 List<String> designs,
													 List<String> sexList,
													 List<String> ethnicities) {
		List<RowForm> groupedSpecimens = new ArrayList<>();
		HashMap<String, NeoStudy> studies = studyHashMap();
		for (String acronym : acronyms) {
			for (String design : designs) {
				NeoStudy study = studies.get(acronym);
				List<String> studyDesigns = new ArrayList<>();
				study.getDesigns().forEach(item -> studyDesigns.add(item.getName()));
				for (String sex: sexList)
					for (String ethinicity : ethnicities) {
						RowForm row = new RowForm();
						row.setAcronym(acronym);
						row.setDesign(design);
						row.setSex(sex);
						row.setEthnicity(ethinicity);
						HashSet<String> biobanks = new HashSet<>();
						int nbSamples = 0;
						for (NeoSpecimen specimen : specimens)
							if (specimen.getAcronym().equals(acronym) &&
									studyDesigns.contains(design) &&
									specimen.getSex().equals(sex) &&
									specimen.getEthnicity().equals(ethinicity)) {

								nbSamples += 1;
								if (specimen.getNoAliquots() > 0)
									biobanks.add(specimen.getBiobankName());
							}
						if (nbSamples > 0) {
							row.setNbSamples(nbSamples);
							row.setBiobanks(new ArrayList<>(biobanks));
							groupedSpecimens.add(row);
						}
					}
			}
		}

		return groupedSpecimens;
	}


	private List<RowForm> groupByDesignAndSexAndType(List<NeoSpecimen> specimens,
														   List<String> acronyms,
														   List<String> designs,
														   List<String> sexList,
														   List<String> specimenTypes) {
		List<RowForm> groupedSpecimens = new ArrayList<>();
		HashMap<String, NeoStudy> studies = studyHashMap();
		for (String acronym : acronyms) {
			for (String design : designs) {
				NeoStudy study = studies.get(acronym);
				List<String> studyDesigns = new ArrayList<>();
				study.getDesigns().forEach(item -> studyDesigns.add(item.getName()));
				for (String sex: sexList)
					for (String type : specimenTypes) {
						NeoSpecType specType = typeRepository.findNeoSpecTypeByName(type);
						RowForm row = new RowForm();
						row.setAcronym(acronym);
						row.setDesign(design);
						row.setSex(sex);
						row.setSpecType(type);
						HashSet<String> biobanks = new HashSet<>();
						int nbSamples = 0;
						for (NeoSpecimen specimen : specimens)
							if (specimen.getAcronym().equals(acronym) &&
									studyDesigns.contains(design) &&
									specimen.getSex().equals(sex) &&
									specimen.getSpecType().getName().equals(specType.getName())) {

								nbSamples += 1;
								if (specimen.getNoAliquots() > 0)
									biobanks.add(specimen.getBiobankName());
							}
						if (nbSamples > 0) {
							row.setNbSamples(nbSamples);
							row.setBiobanks(new ArrayList<>(biobanks));
							groupedSpecimens.add(row);
						}
					}
			}
		}

		return groupedSpecimens;
	}

	private List<RowForm> groupByDiseaseAndSexAndEthnicity(List<NeoSpecimen> specimens,
														   List<String> acronyms,
														   List<String> diseases,
														   List<String> sexList,
														   List<String> ethnicities) {
		List<RowForm> groupedSpecimens = new ArrayList<>();
		HashMap<String, NeoStudy> studies = studyHashMap();
		for (String acronym : acronyms) {
				for (String disease : diseases) {
					for (String sex: sexList) {
						for (String ethinicity : ethnicities) {
							RowForm row = new RowForm();
							row.setAcronym(acronym);
							row.setDisease(disease);
							row.setSex(sex);
							row.setEthnicity(ethinicity);
							HashSet<String> biobanks = new HashSet<>();
							int nbSamples = 0;
							for (NeoSpecimen specimen : specimens) {
								if (specimen.getAcronym().equals(acronym) &&
										specimen.getDisease().equals(disease) &&
										specimen.getSex().equals(sex) &&
										specimen.getEthnicity().equals(ethinicity)) {
									nbSamples += 1;
									if (specimen.getNoAliquots() > 0)
										biobanks.add(specimen.getBiobankName());
								}
							}
							if (nbSamples > 0) {
								row.setNbSamples(nbSamples);
								row.setBiobanks(new ArrayList<>(biobanks));
								groupedSpecimens.add(row);
							}
						}
					}
				}
		}
		return groupedSpecimens;
	}

	private List<RowForm> groupByDesignAndDiseaseAndSexAndType(List<NeoSpecimen> specimens,
														       List<String> acronyms,
														       List<String> designs,
														       List<String> diseases,
														       List<String> sexList,
														       List<String> specimenTypes) {
		List<RowForm> groupedSpecimens = new ArrayList<>();
		HashMap<String, NeoStudy> studies = studyHashMap();
		for (String acronym : acronyms) {
			for (String design : designs) {
				NeoStudy study = studies.get(acronym);
				List<String> studyDesigns = new ArrayList<>();
				study.getDesigns().forEach(item -> studyDesigns.add(item.getName()));
				for (String sex: sexList)
					for (String disease : diseases)
						for (String type : specimenTypes) {
							NeoSpecType specType = typeRepository.findNeoSpecTypeByName(type);
							RowForm row = new RowForm();
							row.setAcronym(acronym);
							row.setDesign(design);
							row.setDisease(disease);
							row.setSex(sex);
							row.setSpecType(type);
							HashSet<String> biobanks = new HashSet<>();
							int nbSamples = 0;
							for (NeoSpecimen specimen : specimens)
								if (specimen.getAcronym().equals(acronym) &&
										studyDesigns.contains(design) &&
										specimen.getDisease().equals(disease) &&
										specimen.getSex().equals(sex) &&
										specimen.getSpecType().getName().equals(specType.getName())) {

									nbSamples += 1;
									if (specimen.getNoAliquots() > 0)
										biobanks.add(specimen.getBiobankName());
								}
							if (nbSamples > 0) {
								row.setNbSamples(nbSamples);
								row.setBiobanks(new ArrayList<>(biobanks));
								groupedSpecimens.add(row);
							}
						}
			}
		}

		return groupedSpecimens;
	}

	private List<RowForm> groupByDesignAndDiseaseAndSexAndCountry(List<NeoSpecimen> specimens,
														       	  List<String> acronyms,
														          List<String> designs,
														          List<String> diseases,
														          List<String> sexList,
														          List<String> countries) {
		List<RowForm> groupedSpecimens = new ArrayList<>();
		HashMap<String, NeoStudy> studies = studyHashMap();
		for (String acronym : acronyms) {
			for (String design : designs) {
				NeoStudy study = studies.get(acronym);
				List<String> studyDesigns = new ArrayList<>();
				study.getDesigns().forEach(item -> studyDesigns.add(item.getName()));
				for (String sex: sexList)
					for (String disease : diseases)
						for (String country : countries) {
							NeoCountry findCountry = countryRepository.findNeoCountryByName(country);
							RowForm row = new RowForm();
							row.setAcronym(acronym);
							row.setDesign(design);
							row.setDisease(disease);
							row.setSex(sex);
							row.setCountry(country);
							HashSet<String> biobanks = new HashSet<>();
							int nbSamples = 0;
							for (NeoSpecimen specimen : specimens)
								if (specimen.getAcronym().equals(acronym) &&
										studyDesigns.contains(design) &&
										specimen.getDisease().equals(disease) &&
										specimen.getSex().equals(sex) &&
										specimen.getCountry().getName().equals(findCountry.getName())) {

									nbSamples += 1;
									if (specimen.getNoAliquots() > 0)
										biobanks.add(specimen.getBiobankName());
								}
							if (nbSamples > 0) {
								row.setNbSamples(nbSamples);
								row.setBiobanks(new ArrayList<>(biobanks));
								groupedSpecimens.add(row);
							}
						}
			}
		}

		return groupedSpecimens;
	}

	private List<RowForm> groupByDesignAndSexAndDisease(List<NeoSpecimen> specimens,
														   List<String> acronyms,
														   List<String> designs,
														   List<String> sexList,
														   List<String> diseases) {
		List<RowForm> groupedSpecimens = new ArrayList<>();
		HashMap<String, NeoStudy> studies = studyHashMap();
		for (String acronym : acronyms) {
			for (String design : designs) {
				NeoStudy study = studies.get(acronym);
				List<String> studyDesigns = new ArrayList<>();
				study.getDesigns().forEach(item -> studyDesigns.add(item.getName()));
				for (String sex: sexList)
					for (String disease : diseases) {
						RowForm row = new RowForm();
						row.setAcronym(acronym);
						row.setDesign(design);
						row.setSex(sex);
						row.setDisease(disease);
						HashSet<String> biobanks = new HashSet<>();
						int nbSamples = 0;
						for (NeoSpecimen specimen : specimens)
							if (specimen.getAcronym().equals(acronym) &&
									studyDesigns.contains(design) &&
									specimen.getSex().equals(sex) &&
									specimen.getDisease().equals(disease)) {

								nbSamples += 1;
								if (specimen.getNoAliquots() > 0)
									biobanks.add(specimen.getBiobankName());
							}
						if (nbSamples > 0) {
							row.setNbSamples(nbSamples);
							row.setBiobanks(new ArrayList<>(biobanks));
							groupedSpecimens.add(row);
						}
					}
			}
		}

		return groupedSpecimens;
	}

	private List<RowForm> groupByDesignAndDisease(List<NeoSpecimen> specimens,
													  List<String> acronyms,
													  List<String> designs,
													  List<String> diseases) {
		List<RowForm> groupedSpecimens = new ArrayList<>();
		HashMap<String, NeoStudy> studies = studyHashMap();
		for (String acronym : acronyms) {
			for (String design : designs) {
				NeoStudy study = studies.get(acronym);
				List<String> studyDesigns = new ArrayList<>();
				study.getDesigns().forEach(item -> studyDesigns.add(item.getName()));
				for (String disease : diseases) {
					RowForm row = new RowForm();
					row.setAcronym(acronym);
					row.setDesign(design);
					row.setDisease(disease);
					HashSet<String> biobanks = new HashSet<>();
					int nbSamples = 0;
					for (NeoSpecimen specimen : specimens)
						if (specimen.getAcronym().equals(acronym) &&
								studyDesigns.contains(design) &&
								specimen.getDisease().equals(disease)) {

							nbSamples += 1;
							if (specimen.getNoAliquots() > 0)
								biobanks.add(specimen.getBiobankName());
						}
					if (nbSamples > 0) {
						row.setNbSamples(nbSamples);
						row.setBiobanks(new ArrayList<>(biobanks));
						groupedSpecimens.add(row);
					}
				}
			}
		}

		return groupedSpecimens;
	}

	private List<RowForm> groupByDesignAndSex(List<NeoSpecimen> specimens,
													 List<String> acronyms,
													 List<String> designs,
													 List<String> sexList) {
		List<RowForm> groupedSpecimens = new ArrayList<>();
		HashMap<String, NeoStudy> studies = studyHashMap();
		for (String acronym : acronyms) {
			for (String design : designs) {
				NeoStudy study = studies.get(acronym);
				List<String> studyDesigns = new ArrayList<>();
				study.getDesigns().forEach(item -> studyDesigns.add(item.getName()));
				for (String sex : sexList) {
					RowForm row = new RowForm();
					row.setAcronym(acronym);
					row.setDesign(design);
					row.setSex(sex);
					HashSet<String> biobanks = new HashSet<>();
					int nbSamples = 0;
					for (NeoSpecimen specimen : specimens)
						if (specimen.getAcronym().equals(acronym) &&
								studyDesigns.contains(design) &&
								specimen.getSex().equals(sex)) {

							nbSamples += 1;
							if (specimen.getNoAliquots() > 0)
								biobanks.add(specimen.getBiobankName());
						}
					if (nbSamples > 0) {
						row.setNbSamples(nbSamples);
						row.setBiobanks(new ArrayList<>(biobanks));
						groupedSpecimens.add(row);
					}
				}
			}
		}

		return groupedSpecimens;
	}

	private List<RowForm> groupByDesign(List<NeoSpecimen> specimens,
											List<String> acronyms,
											List<String> designs) {
		List<RowForm> groupedSpecimens = new ArrayList<>();
		HashMap<String, NeoStudy> studies = studyHashMap();
		for (String acronym : acronyms) {
			for (String design : designs) {
				RowForm row = new RowForm();
				row.setAcronym(acronym);
				row.setDesign(design);
				HashSet<String> biobanks = new HashSet<>();
				int nbSamples = 0;
				NeoStudy study = studies.get(acronym);
				List<String> studyDesigns = new ArrayList<>();
				study.getDesigns().forEach(item -> studyDesigns.add(item.getName()));
				for (NeoSpecimen specimen : specimens) {
					if (specimen.getAcronym().equals(acronym) &&
							studyDesigns.contains(design)) {

						nbSamples += 1;
						if (specimen.getNoAliquots() > 0)
							biobanks.add(specimen.getBiobankName());
					}
				}
				if (nbSamples > 0) {
					row.setNbSamples(nbSamples);
					row.setBiobanks(new ArrayList<>(biobanks));
					groupedSpecimens.add(row);
				}
			}
		}
		return groupedSpecimens;
	}

	private List<RowForm> groupByEthnicity(List<NeoSpecimen> specimens,
											 List<String> acronyms,
											 List<String> ethnicities) {
		List<RowForm> groupedSpecimens = new ArrayList<>();
		for (String acronym : acronyms) {
			for (String ethnicity : ethnicities) {
				RowForm row = new RowForm();
				row.setAcronym(acronym);
				row.setEthnicity(ethnicity);
				HashSet<String> biobanks = new HashSet<>();
				int nbSamples = 0;
				for (NeoSpecimen specimen : specimens)
					if (specimen.getAcronym().equals(acronym) &&
							specimen.getEthnicity().equals(ethnicity)) {

						nbSamples += 1;
						if (specimen.getNoAliquots() > 0)
							biobanks.add(specimen.getBiobankName());
					}
				if (nbSamples > 0) {
					row.setNbSamples(nbSamples);
					row.setBiobanks(new ArrayList<>(biobanks));
					groupedSpecimens.add(row);
				}
			}
		}

		return groupedSpecimens;
	}

	private List<RowForm> groupByCountry(List<NeoSpecimen> specimens,
										   List<String> acronyms,
										   List<String> countries) {
		List<RowForm> groupedSpecimens = new ArrayList<>();
		for (String acronym : acronyms) {
			for (String country : countries) {
				RowForm row = new RowForm();
				row.setAcronym(acronym);
				row.setCountry(country);
				HashSet<String> biobanks = new HashSet<>();
				int nbSamples = 0;
				for (NeoSpecimen specimen : specimens)
					if (specimen.getAcronym().equals(acronym) &&
							specimen.getCountry().equals(country)) {

					nbSamples += 1;
						if (specimen.getNoAliquots() > 0)
							biobanks.add(specimen.getBiobankName());
					}
				if (nbSamples > 0) {
					row.setNbSamples(nbSamples);
					row.setBiobanks(new ArrayList<>(biobanks));
					groupedSpecimens.add(row);
				}
			}
		}

		return groupedSpecimens;
	}

	private List<RowForm> groupByDisease(List<NeoSpecimen> specimens,
										   List<String> acronyms,
										   List<String> diseases) {
		List<RowForm> groupedSpecimens = new ArrayList<>();
		for (String acronym : acronyms) {
			for (String disease : diseases) {
				RowForm row = new RowForm();
				row.setAcronym(acronym);
				row.setDisease(disease);
				HashSet<String> biobanks = new HashSet<>();
				int nbSamples = 0;
				for (NeoSpecimen specimen : specimens)
					if (specimen.getAcronym().equals(acronym) &&
							specimen.getDisease().equals(disease)) {

						nbSamples += 1;
						if (specimen.getNoAliquots() > 0)
							biobanks.add(specimen.getBiobankName());
					}
				if (nbSamples > 0) {
					row.setNbSamples(nbSamples);
					row.setBiobanks(new ArrayList<>(biobanks));
					groupedSpecimens.add(row);
				}
			}
		}

		return groupedSpecimens;
	}

	private List<RowForm> groupBySex(List<NeoSpecimen> specimens,
											List<String> acronyms,
											List<String> sexList) {
		List<RowForm> groupedSpecimens = new ArrayList<>();
		for (String acronym : acronyms) {
			for (String sex : sexList) {
				RowForm row = new RowForm();
				row.setAcronym(acronym);
				row.setSex(sex);
				HashSet<String> biobanks = new HashSet<>();
				int nbSamples = 0;
				for (NeoSpecimen specimen : specimens)
					if (specimen.getAcronym().equals(acronym) &&
							specimen.getSex().equals(sex)) {
						nbSamples += 1;
						if (specimen.getNoAliquots() > 0)
							biobanks.add(specimen.getBiobankName());
					}
				if (nbSamples > 0) {
					row.setNbSamples(nbSamples);
					row.setBiobanks(new ArrayList<>(biobanks));
					groupedSpecimens.add(row);
				}
			}
		}

		return groupedSpecimens;
	}

	private List<RowForm> groupByAcronym(List<NeoSpecimen> specimens,
											List<String> acronyms) {
		List<RowForm> groupedSpecimens = new ArrayList<>();
		for (String acronym : acronyms) {
			RowForm row = new RowForm();
			row.setAcronym(acronym);
			HashSet<String> biobanks = new HashSet<>();
			int nbSamples = 0;
			for (NeoSpecimen specimen : specimens)
				if (specimen.getAcronym().equals(acronym)) {

					nbSamples += 1;
					if (specimen.getNoAliquots() > 0)
						biobanks.add(specimen.getBiobankName());
				}
			if (nbSamples > 0) {
				row.setNbSamples(nbSamples);
				row.setBiobanks(new ArrayList<>(biobanks));
				groupedSpecimens.add(row);
			}
		}

		return groupedSpecimens;
	}
}
