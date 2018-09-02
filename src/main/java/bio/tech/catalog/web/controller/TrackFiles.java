package bio.tech.catalog.web.controller;

import bio.tech.catalog.persistence.dao.RoleRepository;
import bio.tech.catalog.persistence.model.Role;
import bio.tech.catalog.persistence.model.User;
import bio.tech.catalog.service.UserService;
import bio.tech.catalog.utils.CsvToNeo;
import bio.tech.catalog.utils.TrackCSVFiles;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.neo4j.ogm.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.*;

@Controller
public class TrackFiles {

    private static List<Map<String, String>> studyCollectibleAttributes = createCollectibleAttributesMap("Study");
    private static List<Map<String, String>> participantCollectibleAttributes = createCollectibleAttributesMap("Participant");
    private static List<Map<String, String>> createCollectibleAttributesMap(String entity) {
        List<Map<String, String>> newList = new ArrayList<>();
        Map<String, String> m = new LinkedHashMap<>();
        m.put("header", "SMOKING");
        m.put("name", "smoking");
        m.put("desc", "Smoking history");
        newList.add(m);
        m = new LinkedHashMap<>();
        m.put("header", "ALCOHOL");
        m.put("name", "alcohol_use");
        m.put("desc", "Alcohol use history");
        newList.add(m);
        m = new LinkedHashMap<>();
        m.put( "header", "SUBSTANCE");
        m.put("name", "substance_use");
        m.put("desc", "Psychoactive substance use");
        newList.add(m);
        m = new LinkedHashMap<>();
        m.put("header", "BLOOD PRESSURE");
        m.put("name", "blood_pressure");
        m.put("desc", "Blood Pressure measurement");
        newList.add(m);
        m = new LinkedHashMap<>();
        m.put("header", "DIET");
        m.put("name", "diet");
        m.put("desc", "diet");
        newList.add(m);
        m = new LinkedHashMap<>();
        m.put("header", "HIV STATUS");
        m.put("name", "HIV status");
        m.put("desc", "HIV status");
        newList.add(m);
        if (entity.equals("Participant")) {
            m = new LinkedHashMap<>();
            m.put("header", "HIV STATUS");
            m.put("name", "HIV status");
            m.put("desc", "HIV status");
            newList.add(m);
        }

        return(newList);
    }

    @Autowired
    private UserService userService;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private Session session;

    @Autowired
    private CsvToNeo csvToNeo;

    /*
     * List Uploaded files
     */

    @GetMapping(value = "/track")
    public String trackCSVFiles(@RequestParam(name="biobank", defaultValue="") String biobankName, Model model) {

        User user = authenticatedUser();
        User newUser = null;
        if (biobankName != null && !biobankName.isEmpty())
            newUser = userService.findUserByUsername(biobankName);
        else
            newUser = user;

        List<TrackCSVFiles> rawFiles = userCSVFiles("raw", newUser);
        List<TrackCSVFiles> processedFiles = userCSVFiles("processed", newUser);
        model.addAttribute("rawFiles", rawFiles);
        model.addAttribute("processedFiles", processedFiles);

        if (isAdmin(user)) {
            Collection<User> users = userService.findUsersByRole("ROLE_BIOBANK");
            model.addAttribute("users", users);
        }

        Role role = user.getRoles().stream().findFirst().orElse(null);
        model.addAttribute("role", role.getName());
        model.addAttribute("username", user.getUsername());

        return "track";
    }

    /*
     * Delete an uploaded file
     */

    @PostMapping(value = "/track", params = {"delete"})
    public String deleteCSVFile(HttpServletRequest request, RedirectAttributes redirectAttributes) throws IOException {
        String pathToFileToDelete = request.getParameter("delete");
        //File fileToDelete = new ClassPathResource("static" + pathToFileToDelete).getFile();
        File fileToDelete = new File("." + pathToFileToDelete);
        Boolean deleted = false;
        if (fileToDelete.exists())
            deleted = fileToDelete.delete();

        if (deleted)
            redirectAttributes.addFlashAttribute("success", "File deleted successfully!");
        else
            redirectAttributes.addFlashAttribute("error", "Error: File not found!");

        return "redirect:/track";
    }

    /*
     * Process an uploaded file
     */
    @RequestMapping(value = "/track", params = {"process"}, method = RequestMethod.POST)
    public String processCSVNeo(HttpServletRequest request, RedirectAttributes redirectAttributes) throws Exception{

        String[] parameters = request.getParameter("fileOwnerRole").split("-");
        String fileOwner = parameters[0];
        String fileRole = parameters[1];
        String userRole = parameters[2];

        if (fileRole.isEmpty() || !userRole.equals("ROLE_ADMIN")) return "redirect:/logout";

        File file = new File("./" + request.getParameter("process"));
        String path = file.getAbsolutePath();
        User user = userService.findUserByUsername(fileOwner);
        String query = "";
        if (fileRole.equals("ROLE_ARCHIVE")) {
            query = constructArchiveQuery("file:///" + path, file);
            runNeoQuery(query);
        } else if (fileRole.equals("ROLE_BIOBANK")) {
            csvToNeo.setCsvFilename(path);
            csvToNeo.setToEmail(user.getEmail());
            csvToNeo.setBiobankName(user.getInstitutionName());
            csvToNeo.parseBiobankCSVFile();
        }

        boolean fileRenamed = file.renameTo(new File("./users/" + user.getUsername() + "/processed/" + file.getName()));
        if (!fileRenamed)
            redirectAttributes.addFlashAttribute("error", "File could not be renamed!");
        else
            redirectAttributes.addFlashAttribute("success", "File process succeeded!");

        return  "redirect:/track?biobank=" + fileOwner;
    }

    /*
     * NON API
     */

    private User authenticatedUser() {
        final org.springframework.security.core.userdetails.User authenticated = (org.springframework.security.core.userdetails.User)
                SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return userService.findUserByEmail(authenticated.getUsername());
    }

    private boolean isAdmin(User user) {
        Collection<Role> roles = user.getRoles();
        for (Role role : roles) {
            if (role.getName().equals("ROLE_ADMIN")) {
                return true;
            }
        }
        return false;
    }

    private List<TrackCSVFiles> userCSVFiles(String folderName, User user) {
        List<TrackCSVFiles> files = null;
        try {
            File folder = new File("./users/" + user.getUsername() + "/" + folderName);
            Role role = roleRepository.findByUserId(user.getId()).stream().findFirst().orElse(null);
            files = listCSVFiles(user.getUsername(), role.getName(), folder, folderName);
        } catch(NullPointerException ex) {
            System.err.println("Err: folder: " + folderName + " does'nt not exists!");
        }
        return files;
    }

    private List<TrackCSVFiles> listCSVFiles(String username, String role, File folder, String type) throws NullPointerException {
        List<TrackCSVFiles> listRet = new ArrayList<>();

        for (String fileName : folder.list()) {
            String path = "/users/" + username + "/raw/" + fileName;
            TrackCSVFiles file = new TrackCSVFiles(path, fileName, username, role, "raw");
            listRet.add(file);
        }

        return listRet;
    }

    private String constructArchiveQuery(String path, File file) {
        String query = "";
        String fileType = file.getName().split("_")[1];
        if (fileType.equals("study")) {

            query += "LOAD CSV WITH HEADERS FROM \"" + path + "\" AS row ";
            query += "MERGE (st:NeoStudy {acronym: row.ACRONYM}) ";
            query += "SET ";
            query += "st.title= row.TITLE, st.description= row.DESCRIPTION, " +
                    "st.totalSpecimens=toInt(row.`TOTAL SPECIMENS`), " +
                    "st.noCases=toInt(row.`NO OF CASES`), " +
                    "st.noControls=toInt(row.`NO OF CONTROLS`), " +
                    "st.consentCode=row.`CONSENT CODE`, " +
                    "st.disease=row.`PRIMARY DISEASE` ";
            query += "MERGE (d:NeoDesign {name: row.`STUDY DESIGN`}) ";
            query += "MERGE (ds:NeoDataSet {egaAccess: row.`EGA ACCESS`, description: row.`DESCRIPTION DATASET`}) ";
            query += "FOREACH ( i IN SPLIT(row.`DATA GENERATION`, ',') | " +
                    "MERGE (dg:NeoDataGen {name: i}) " +
                    "MERGE (ds)-[:HAS_DATAGEN]->(dg)) ";
            query += "FOREACH (j IN SPLIT(row.TECHNOLOGY, ',') | " +
                    "MERGE (t:NeoTechnology {name: j}) " +
                    "MERGE (ds)-[:HAS_TECHNOLOGY]->(t)) ";
            query += "FOREACH (k IN SPLIT(row.`FILE TYPES`, ',') | " +
                    "MERGE (ft:NeoFileType {name: k}) " +
                    "MERGE (ds)-[:HAS_FILETYPE]->(ft)) ";
            query += "MERGE (st)-[:STUDY_DESIGN]->(d) ";
            query += "MERGE (st)-[:HAS_DATASET]->(ds)";

            query = queryCollectibleAttributes(query, studyCollectibleAttributes);

        } else if (fileType.equals("individual")) {
            query += "LOAD CSV WITH HEADERS FROM \"" + path + "\" AS row ";
            query += "MATCH(st:NeoStudy {acronym: row.ACRONYM}) ";
            query += "WITH st, row, count(*) AS c ";
            query += "WHERE c = 1 ";

            query += "MERGE (p:NeoParticipant {participantId: row.`PARTICIPANT ID`}) ";
            query += "SET ";
            query += "p.age= toInt(row.AGE), p.bmi= toFloat(row.BMI), p.diseaseStatus= row.`DISEASE STATUS`, " +
                    "p.height= toInt(row.HEIGHT), p.weight= toFloat(row.WEIGHT) ";
            query += "MERGE (g:NeoGender {name: row.GENDER}) ";
            query += "FOREACH (i IN CASE WHEN row.ETHNICITY IS NULL THEN [] ELSE [1] END | " +
                    "MERGE (e:NeoEthnicity {name: row.ETHNICITY}) " +
                    "MERGE (p)-[:HAS_ETHNICITY]->(e)) ";
            query += "MERGE (cc:NeoCharacter {name: row.`CASE CONTROL`}) ";
            query += "MERGE (p)-[:HAS_GENDER]->(g) ";
            query += "MERGE (p)-[:HAS_CHARACTER]->(cc) ";
            query += "MERGE (st)-[:HAS_PARTICIPANT]->(p) ";

            query = queryCollectibleAttributes(query, participantCollectibleAttributes);

        }

        return query;
    }

    private String queryCollectibleAttributes(String query, List<Map<String, String>> ca) {

        int count = 1;
        StringBuilder sb = new StringBuilder(1000);
        sb.append(query);
        String attributeQueryTemplate = "MERGE (a${count}:NeoStudyAttribute {name: '${name}', description: '${desc}'}) " +
                "FOREACH (s${count} in CASE WHEN row.`${header}` = 'Yes' THEN [st] ELSE [] END " +
                "| MERGE (s${count}) -[:COLLECTS] -> (a${count})) ";

        for (Map<String, String> m : ca) {
            m.put("count", Integer.toString(count));
            StrSubstitutor sub = new StrSubstitutor(m);
            String attributeQuery = sub.replace(attributeQueryTemplate) + "\n";
            sb.append(attributeQuery);
            count++;
        }

        return sb.toString();
    }

    private void runNeoQuery(String query) {
        LinkedHashMap<String, String> paramsQuery = new LinkedHashMap<>();
        session.query(query, paramsQuery);
    }
}
