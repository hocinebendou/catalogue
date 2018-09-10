package bio.tech.catalog.web.controller;

import bio.tech.catalog.persistence.dao.SpecimenRepository;
import bio.tech.catalog.persistence.dao.StudyRepository;
import bio.tech.catalog.persistence.model.*;
import org.neo4j.ogm.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class StudyController {

    @Autowired
    private SpecimenRepository specimenRepository;

    @Autowired
    private StudyRepository studyRepository;

    @Autowired
    Session session;

    @RequestMapping("/study")
    public String studyInfo(HttpServletRequest request, Model model) {
        String studyAcronym = request.getParameter("s");
        NeoStudy study = studyRepository.findByAcronym(studyAcronym);
        int countStudies = studyRepository.countStudies();
        model.addAttribute("countStudies", countStudies);
        int countSamples = specimenRepository.findAll().size();
        model.addAttribute("countSamples", countSamples);

        List<NeoDataSet> dataSets = study.getDataSets();
        Map<String, List<NeoDataGen>> dataGens = new HashMap<>();
        Map<String, List<NeoFileType>> fileTypes = new HashMap<>();
        Map<String, List<NeoTechnology>> technologies = new HashMap<>();
        for (NeoDataSet dataSet: dataSets) {
            dataSet = session.load(NeoDataSet.class, dataSet.getId());
            dataGens.put(dataSet.getEgaAccess(), dataSet.getDataGens());
            fileTypes.put(dataSet.getEgaAccess(), dataSet.getFileTypes());
            technologies.put(dataSet.getEgaAccess(), dataSet.getTechnologies());
        }
        model.addAttribute("datagen", dataGens);
        model.addAttribute("filetype", fileTypes);
        model.addAttribute("technology", technologies);
        model.addAttribute("dataset", dataSets);
        model.addAttribute("study", study);
        return "study";
    }
}
