package bio.tech.catalog.web.api;

import bio.tech.catalog.persistence.dao.*;
import bio.tech.catalog.persistence.model.*;
import bio.tech.catalog.service.UserService;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class DbacApiController {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private QueryRepository queryRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ParticipantRepository participantRepository;

    @Autowired
    private SpecimenRepository specimenRepository;

    @Autowired
    private SpecQueryRepository specQueryRepository;

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/requested/carts", method = RequestMethod.GET)
    public ResponseEntity<JSONObject> requestedCarts() {

        List<NeoCart> carts = cartRepository.findNeoCartByStatus("Requested");

        JSONObject obj = new JSONObject();
        obj.put("requestedCarts", carts);

        return new ResponseEntity<>(obj, HttpStatus.OK);
    }

    @RequestMapping(value = "/approved/carts", method = RequestMethod.GET)
    public ResponseEntity<JSONObject> approvedCarts() {

        List<NeoCart> carts = cartRepository.findNeoCartByStatus("Approved");

        JSONObject obj = new JSONObject();
        obj.put("approvedCarts", carts);

        return new ResponseEntity<>(obj, HttpStatus.OK);
    }

    @RequestMapping(value = "/rejected/carts", method = RequestMethod.GET)
    public ResponseEntity<JSONObject> rejectedCarts() {

        List<NeoCart> carts = cartRepository.findNeoCartByStatus("Rejected");

        JSONObject obj = new JSONObject();
        obj.put("rejectedCarts", carts);

        return new ResponseEntity<>(obj, HttpStatus.OK);
    }

    @RequestMapping(value = "/dbac/cart/status", method = RequestMethod.POST)
    public ResponseEntity<JSONObject> changeStatusCart(@RequestBody Map<String, Object> body) {

        JSONObject obj = new JSONObject();

        String cartId = (String) body.get("cartId");
        String projectId = (String) body.get("projectId");
        String username = (String) body.get("username");
        String status = (String) body.get("status");

        NeoProject project = projectRepository.projectByUsernameAndProjectId(username, projectId);
        NeoCart cart = cartRepository.findNeoCartByProjectIdAndCartId(project.getId(), cartId);
        cart.setStatus(status);

        cartRepository.save(cart);

        if (status.equals("Approved"))
            obj.put("message", "Cart status set, successfully, to approved.");
        else if (status.equals("Rejected"))
            obj.put("message", "Cart status set to Rejected.");
        else
            obj.put("message", "Cart status set to Requested.");

        return new ResponseEntity<>(obj, HttpStatus.OK);
    }

    @RequestMapping(value = "/dbac/linkSpecimenQuery", method = RequestMethod.POST)
    public ResponseEntity<JSONObject> linkBiospecimenQuery(@RequestBody Map<String, Object> body) {

        JSONObject obj = new JSONObject();
        String specimenId = (String) body.get("id");
        long queryId = (int) body.get("queryId");
        int nbRequest = Integer.parseInt((String) body.get("nbRequest"));

        NeoSpecQuery specQuery = specQueryRepository.findNeoSpecQueryBySpecimenIdAndQueryId(specimenId, queryId);

        if (specQuery != null) {
            obj.put("error", "Biospecimen, " + specimenId + ", already associated to the query, " + queryId);
            obj.put("message", null);
        } else {
            specQuery = new NeoSpecQuery();
            specQuery.setSpecimenId(specimenId);
            specQuery.setQueryId(queryId);
            specQuery.setNbRequest(nbRequest);

            specQueryRepository.save(specQuery);

            obj.put("message", "Biospecimen, " + specimenId + ", has been associated to the query, " + queryId);
            obj.put("error", null);
        }

        return new ResponseEntity<>(obj, HttpStatus.OK);
    }

    @RequestMapping(value = "/dbac/unlinkSpecimenQuery", method = RequestMethod.POST)
    public ResponseEntity<JSONObject> unlinkSpecimenQuery(@RequestBody Map<String, Object> body) {

        JSONObject obj = new JSONObject();
        String specimenId = (String) body.get("id");
        long queryId = (int) body.get("queryId");

        NeoSpecQuery specQuery = specQueryRepository.findNeoSpecQueryBySpecimenIdAndQueryId(specimenId, queryId);

        if (specQuery == null) {
            obj.put("error", "Biospecimen, " + specimenId + ", has no query, " + queryId  + ", associated!");
            obj.put("message", null);
        } else {
            specQueryRepository.delete(specQuery);
            obj.put("message", "Biospecimen, " + specimenId + ", has been successfully deassociated from the query, " + queryId);
            obj.put("error", null);
        }

        return new ResponseEntity<>(obj, HttpStatus.OK);
    }

    @RequestMapping(value = "/dbac/specimen/queries/{id}/{queryId}", method = RequestMethod.GET)
    public ResponseEntity<JSONObject> biospecimenQueries(@PathVariable("id") String specimenId,
                                                         @PathVariable("queryId") String queryId) {
        JSONObject obj = new JSONObject();

        List<NeoSpecQuery> queries = specQueryRepository.findNeoSpecQueriesBySpecimenIdAndQueryId(
                specimenId, Long.valueOf(queryId));

        obj.put("queries", queries);

        return new ResponseEntity<>(obj, HttpStatus.OK);
    }

    @RequestMapping(value = "/dbac/cart/project/{id}", method = RequestMethod.GET)
    public ResponseEntity<JSONObject> cartProject(@PathVariable("id") String id) {

        JSONObject obj = new JSONObject();
        NeoProject project = projectRepository.projectByCartLongId(Long.parseLong(id));

        obj.put("project", project);

        return new ResponseEntity<>(obj, HttpStatus.OK);
    }

    @RequestMapping(value = "/dbac/cart/user/{id}", method = RequestMethod.GET)
    public ResponseEntity<JSONObject> cartUser(@PathVariable("id") String id) {

        JSONObject ret = new JSONObject();
        JSONObject obj = new JSONObject();
        User user = userService.findUserByCartId(Long.parseLong(id));


        obj.put("firstName", user.getFirstName());
        obj.put("lastName", user.getLastName());
        obj.put("email", user.getEmail());
        obj.put("institution", "University of the Western Cape");

        ret.put("user", obj);
        return new ResponseEntity<>(ret, HttpStatus.OK);
    }

    @RequestMapping(value = "/dbac/query/biospecimens/{id}", method = RequestMethod.GET)
    public ResponseEntity<JSONObject> queryBiospecimens(@PathVariable("id") String id) {
        NeoQuery query = queryRepository.findOne(Long.parseLong(id));
        List<String> pIds = participantIds(query);

        List<NeoSpecimen> specs = specimens(query, pIds);

        List<JSONObject> objs = new ArrayList<>();
        for (NeoSpecimen spec : specs) {

            JSONObject obj = new JSONObject();
            obj.put("id", spec.getSampleId());
            obj.put("biobank", spec.getBiobankName());
            obj.put("aliquots", spec.getNoAliquots());

            NeoSpecQuery specQuery = specQueryRepository.findNeoSpecQueryBySpecimenIdAndQueryId(
                    spec.getSampleId(), Long.parseLong(id));
            if (specQuery != null)
                obj.put("linked", true);
            else
                obj.put("linked", false);

            objs.add(obj);
        }

        JSONObject ret = new JSONObject();
        ret.put("specimens", objs);

        return new ResponseEntity<>(ret, HttpStatus.OK);
    }

    private List<String> participantIds(NeoQuery query) {
        List<String> ids = new ArrayList<>();
        String acronym = query.getAcronym();
        String sex = query.getSex();
        String ethnicity = query.getEthnicity();

        if (sex != null) {
            ids.addAll(participantRepository.findByAcronymsAndSex(
                    new ArrayList<>(Arrays.asList(acronym)),
                    new ArrayList<>(Arrays.asList(sex))));
        }

        if (ethnicity != null) {
            ids.retainAll(participantRepository.findByAcronymsAndEthnicity(
                    new ArrayList<>(Arrays.asList(acronym)),
                    new ArrayList<>(Arrays.asList(ethnicity))));
        }

        return ids;
    }

    private List<NeoSpecimen> specimens(NeoQuery query, List<String> pIds) {
        List<NeoSpecimen> specs = new ArrayList<>();

        String acronym = query.getAcronym();
        String country = query.getCountry();
        String specimenType = query.getSpecimenType();

        if (!pIds.isEmpty()) {
            specs.addAll(specimenRepository.findByParticipantIds(pIds));
        } else {
            specs.addAll(specimenRepository.findByAcronyms(
                    new ArrayList<>(Arrays.asList(acronym))));
        }

        if (country != null) {
            if (specs.isEmpty()) {
                specs.addAll(specimenRepository.findByAcronymsAndCountries(
                        new ArrayList<>(Arrays.asList(acronym)),
                        new ArrayList<>(Arrays.asList(country))));
            } else {
                specs.retainAll(specimenRepository.findByAcronymsAndCountries(
                        new ArrayList<>(Arrays.asList(acronym)),
                        new ArrayList<>(Arrays.asList(country))));
            }
        }

        if (specimenType != null) {
            if (specs.isEmpty()) {
                specs.addAll(specimenRepository.findByAcronymsAndTypes(
                        new ArrayList<>(Arrays.asList(acronym)),
                        new ArrayList<>(Arrays.asList(specimenType))));
            } else {
                specs.retainAll(specimenRepository.findByAcronymsAndTypes(
                        new ArrayList<>(Arrays.asList(acronym)),
                        new ArrayList<>(Arrays.asList(specimenType))));
            }
        }
        return specs;
    }
}
