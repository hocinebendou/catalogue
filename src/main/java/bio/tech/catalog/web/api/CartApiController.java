package bio.tech.catalog.web.api;

import bio.tech.catalog.persistence.dao.CartRepository;
import bio.tech.catalog.persistence.dao.DataQueryRepository;
import bio.tech.catalog.persistence.dao.ProjectRepository;
import bio.tech.catalog.persistence.dao.QueryRepository;
import bio.tech.catalog.persistence.model.*;
import bio.tech.catalog.service.UserService;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/api")
public class CartApiController {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private QueryRepository queryRepository;

    @Autowired
    private DataQueryRepository dataQueryRepository;

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/cart/remove/query/{id}", method = RequestMethod.GET)
    public ResponseEntity<JSONObject> removeBioQuery(@PathVariable("id") String id) {

        queryRepository.delete(Long.parseLong(id));

        JSONObject obj = new JSONObject();
        obj.put("message", "Query deleted successfully.");

        return new ResponseEntity<>(obj, HttpStatus.OK);
    }

    @RequestMapping(value = "/cart/remove/dataset/{id}", method = RequestMethod.GET)
    public ResponseEntity<JSONObject> removeDataQuery(@PathVariable("id") String id) {

        dataQueryRepository.delete(Long.parseLong(id));

        JSONObject obj = new JSONObject();
        obj.put("message", "Query deleted successfully.");

        return new ResponseEntity<>(obj, HttpStatus.OK);
    }

    @RequestMapping(value = "/cart/queries/{id}", method = RequestMethod.GET)
    public ResponseEntity<JSONObject> selectedCart(@PathVariable("id") String idCart) {

        NeoCart cart = cartRepository.findOne(Long.parseLong(idCart));
        List<NeoQuery> queries = queryRepository.cartQueries(cart.getId());

        JSONObject obj = new JSONObject();
        obj.put("cart", cart);
        obj.put("queries", queries);

        return new ResponseEntity<>(obj, HttpStatus.OK);
    }

    @RequestMapping(value = "/carts/{id}", method = RequestMethod.GET)
    public ResponseEntity<JSONObject> userProjectCarts(@PathVariable("id") String projectId) {
        User user = authenticatedUser();

        NeoProject project = projectRepository.projectByUsernameAndProjectId(user.getUsername(), projectId);
        List<NeoCart> carts = cartRepository.projectCarts(project.getId());

        List<JSONObject> jsonCartQueries = new ArrayList<>();
        for (NeoCart cart : carts) {
            JSONObject obj = new JSONObject();
            obj.put("cart", cart);
            List<NeoQuery> queries = queryRepository.cartQueries(cart.getId());
            obj.put("queries", queries);
            List<NeoDataQuery> dataQueries = dataQueryRepository.cartQueries(cart.getId());
            obj.put("data-queries", dataQueries);
            jsonCartQueries.add(obj);
        }

        JSONObject response = new JSONObject();
        response.put("cart-queries", jsonCartQueries);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(value = "/cart/create", method = RequestMethod.POST)
    public ResponseEntity<JSONObject> createCart(@RequestBody Map<String, Object> body) {

        JSONObject obj = new JSONObject();

        String cartId = (String) body.get("cartId");
        String projectId = (String) body.get("projectId");

        User user = authenticatedUser();
        NeoProject project = projectRepository.projectByUsernameAndProjectId(user.getUsername(), projectId);

        NeoCart cart = new NeoCart();
        cart.setCardId(cartId);
        cart.setStatus("Pending");
        cart.setUsername(user.getUsername());
        cart.setProjectId(projectId);
        cartRepository.save(cart);

        project.addCart(cart);
        projectRepository.save(project);

        return new ResponseEntity<>(obj, HttpStatus.OK);
    }

    @RequestMapping(value = "cart/remove/{id}", method = RequestMethod.GET)
    public ResponseEntity<JSONObject> deleteCart(@PathVariable("id") String cartId) {

        NeoCart cart = cartRepository.findOne(Long.parseLong(cartId));

        String successMessage = null;
        String failureMessage = null;
        if (cart.getStatus().equals("Pending")) {
            cartRepository.delete(cart);
            successMessage = "Cart, " + cart.getCartId() + ", has been successfully deleted.";
        } else {
            failureMessage = "Cart, " + cart.getCartId() + ", is in '" + cart.getStatus() + "', and " +
                    "cannot be deleted.";
        }

        JSONObject obj = new JSONObject();
        obj.put("successMessage", successMessage);
        obj.put("failureMessage", failureMessage);

        return new ResponseEntity<>(obj, HttpStatus.OK);
    }

    @RequestMapping(value = "/cart/request/{id}", method = RequestMethod.GET)
    public ResponseEntity<JSONObject> requestCart(@PathVariable("id") String idCart) {

        JSONObject ret = new JSONObject();
        NeoCart cart = cartRepository.findOne(Long.parseLong(idCart));

        cart.setStatus("Requested");
        cartRepository.save(cart);

        JSONObject obj = new JSONObject();
        obj.put("message", "Cart " + cart.getCartId() + " is set to 'Requested'");
        obj.put("cart", cart);

        ret.put("return", obj);
        return new ResponseEntity<>(ret, HttpStatus.OK);
    }

    @RequestMapping(value = "/cart/update", method = RequestMethod.POST, consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<JSONObject> updateCart(@RequestBody Map<String, List<Object>> body) {

        JSONObject ret = new JSONObject();

        int idCart = (int) body.get("dbId").get(0);
        String newCartId = (String) body.get("newCartId").get(0);

        NeoCart cart = cartRepository.findOne(Long.valueOf(idCart));
        if (!cart.getCartId().equals(newCartId))
            cart.setCardId(newCartId);

        // Biospecimens request
        List<NeoQuery> queries = new ArrayList<>();
        for (Object row : body.get("bioRows")) {
            LinkedHashMap<String, Object> map = (LinkedHashMap<String, Object>) row;
            NeoQuery query = new NeoQuery();
            if (map.containsKey("id")) {
                query = queryRepository.findOne(Long.valueOf(map.get("id").toString()));
            }
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                switch (entry.getKey()) {
                    case "acronym":
                        query.setAcronym((String)entry.getValue());
                        break;
                    case "design":
                        query.setDesign((String)entry.getValue());
                        break;
                    case "disease":
                        query.setDisease((String)entry.getValue());
                        break;
                    case "nbRequest":
                        query.setNbRequest((String)entry.getValue());
                        break;
                    case "sex":
                        query.setSex((String)entry.getValue());
                        break;
                    case "ethnicity":
                        query.setEthnicity((String)entry.getValue());
                        break;
                    case "country":
                        query.setCountry((String)entry.getValue());
                        break;
                    case "specType":
                        query.setSpecimenType((String)entry.getValue());
                        break;
                    case "smoking":
                        query.setSmoking((boolean)entry.getValue());
                    case "diet":
                        query.setDiet((boolean)entry.getValue());
                    case "hivStatus":
                        query.setHivStatus((boolean)entry.getValue());
                    case "bloodPressure":
                        query.setBloodPressure((boolean)entry.getValue());
                    case "alcoholUse":
                        query.setAlcoholUse((boolean)entry.getValue());

                }
            }
            queries.add(query);
        }

        if (!queries.isEmpty()) {
            queryRepository.save(queries);
            cart.setQueries(queries);
        }

        // Datasets request
        List<NeoDataQuery> dataQueries = new ArrayList<>();
        for (Object row : body.get("dataRows")) {
            LinkedHashMap<String, String> map = (LinkedHashMap<String, String>) row;
            NeoDataQuery query = new NeoDataQuery();
            if (map.containsKey("id")) {
                query = dataQueryRepository.findOne(Long.valueOf(map.get("id")));
            }
            for (Map.Entry<String, String> entry : map.entrySet()) {
                switch (entry.getKey()) {
                    case "acronym":
                        query.setAcronym(entry.getValue());
                        break;
                    case "egaAccess":
                        query.setEgaAccess(entry.getValue());
                        break;
                }
                dataQueries.add(query);
            }
        }

        if (!dataQueries.isEmpty()) {
            dataQueryRepository.save(dataQueries);
            cart.setDataQueries(dataQueries);
        }

        cartRepository.save(cart);

        JSONObject obj = new JSONObject();
        obj.put("cart", cart);
        obj.put("message", "Cart saved successfully.");

        ret.put("return", obj);
        return new ResponseEntity<>(ret, HttpStatus.OK);
    }

    /*
     * NON API
     */

    private bio.tech.catalog.persistence.model.User authenticatedUser() {
        final org.springframework.security.core.userdetails.User authenticated = (org.springframework.security.core.userdetails.User)
                SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return userService.findUserByEmail(authenticated.getUsername());
    }
}
