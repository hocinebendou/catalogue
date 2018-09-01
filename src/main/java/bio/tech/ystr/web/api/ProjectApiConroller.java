package bio.tech.ystr.web.api;

import bio.tech.ystr.persistence.dao.CartRepository;
import bio.tech.ystr.persistence.dao.ProjectRepository;
import bio.tech.ystr.persistence.model.NeoCart;
import bio.tech.ystr.persistence.model.NeoProject;
import bio.tech.ystr.persistence.model.User;
import bio.tech.ystr.service.UserService;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ProjectApiConroller {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private CartRepository cartRepository;

    @RequestMapping(value = "/projects", method = RequestMethod.GET)
    public ResponseEntity<JSONObject> projectCards() {

        User user = authenticatedUser();
        List<NeoProject> projects = projectRepository.userProjects(user.getUsername());
        List<JSONObject> projectCards = new ArrayList<>();
        for (NeoProject project : projects) {
            JSONObject obj = new JSONObject();
            obj.put("project", project);
            List<NeoCart> approvedCards = new ArrayList<>();
            List<NeoCart> requestedCards = new ArrayList<>();
            List<NeoCart> pendingCards = new ArrayList<>();
            List<NeoCart> carts = cartRepository.projectCarts(project.getId());
            for (NeoCart cart : carts) {
                switch (cart.getStatus()) {
                    case "Approved":
                        approvedCards.add(cart);
                        break;
                    case "Requested":
                        requestedCards.add(cart);
                        break;
                    case "Pending":
                        pendingCards.add(cart);
                        break;
                }
            }
            obj.put("approved-cards", approvedCards);
            obj.put("requested-cards", requestedCards);
            obj.put("pending-cards", pendingCards);
            projectCards.add(obj);
        }

        JSONObject response = new JSONObject();
        response.put("project-cards", projectCards);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(value = "/create/project", method = RequestMethod.POST)
    public ResponseEntity<Object> createProject(@Valid @RequestBody NeoProject project,
                                                BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            System.out.println(bindingResult.getAllErrors());
        }

        User user = authenticatedUser();

        project.setUser(new ArrayList<>(Arrays.asList(user)));
        NeoProject savedProject = projectRepository.save(project);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(savedProject.getId()).toUri();

        return ResponseEntity.created(location).build();
    }

    @RequestMapping(value = "/remove/{id}", method = RequestMethod.GET)
    public ResponseEntity<JSONObject> deleteProject(@PathVariable("id") String projectId) {

        NeoProject project = projectRepository.findNeoProjectByProjectId(projectId);
        // TODO: don't delete if the project has validated carts (to decide).
        projectRepository.deleteByProjectId(projectId);

        JSONObject obj = new JSONObject();
        obj.put("message", "Project with id " + projectId + " has been deleted");

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
}
