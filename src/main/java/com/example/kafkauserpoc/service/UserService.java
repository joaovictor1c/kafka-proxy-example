package  com.example.kafkauserpoc.service;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import io.opentelemetry.api.trace.StatusCode;
import org.springframework.stereotype.Service;

import com.example.kafkauserpoc.model.User;
import com.example.kafkauserpoc.repository.UserRepository;


import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private Tracer tracer;

    public List<User> findAll() {
        Span span = tracer.spanBuilder("findAllUsers")
                .setParent(Context.current())
                .startSpan();
        try {
            List<User> users = userRepository.findAll();
            span.setStatus(StatusCode.OK);
            return users;
        } catch (Exception e) {
            span.setStatus(StatusCode.ERROR, e.getMessage());
            throw e;
        } finally {
            span.end();
        }
    }

    public Optional<User> findById(Long id) {
        Span span = tracer.spanBuilder("findUserById")
                .setParent(Context.current())
                .setAttribute("user.id", id)
                .startSpan();
        try {
            Optional<User> user = userRepository.findById(id);
            span.setStatus(StatusCode.OK);
            return user;
        } catch (Exception e) {
            span.setStatus(StatusCode.ERROR, e.getMessage());
            throw e;
        } finally {
            span.end();
        }
    }

    public User save(User user) {
        return createUser(user);
    }

    public User createUser(User user) {
        Span span = tracer.spanBuilder("createUser")
                .setParent(Context.current())
                .setAttribute("user.name", user.getName())
                .setAttribute("user.email", user.getEmail())
                .startSpan();

        try {
            Span saveSpan = tracer.spanBuilder("saveUser")
                    .setParent(Context.current().with(span))
                    .startSpan();
            try {
                User savedUser = userRepository.save(user);
                saveSpan.setStatus(StatusCode.OK);
                span.addEvent("User created successfully");
                return savedUser;
            } catch (Exception e) {
                saveSpan.setStatus(StatusCode.ERROR, "Save failed");
                throw e;
            } finally {
                saveSpan.end();
            }
        } catch (Exception e) {
            span.setStatus(StatusCode.ERROR, e.getMessage());
            throw e;
        } finally {
            span.end();
        }
    }


} 