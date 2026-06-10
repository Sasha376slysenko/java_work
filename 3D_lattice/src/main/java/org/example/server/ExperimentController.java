package org.example.server;

import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/experiments")
public class ExperimentController {
    private final ExperimentRepository experimentRepository;

    public ExperimentController(ExperimentRepository experimentRepository) {
        /*
        * +-----------------------------------------------+
        * |Впровадження залежності (Dependency Injection).|
        * +-----------------------------------------------+
        */
        this.experimentRepository = experimentRepository;
    }

    @PostMapping
    public Experiment createExperiment(@RequestBody Experiment experiment) {
        /*
        * +----------+
        * |Save data.|
        * +----------+
        */
        if (experiment.getDataPoints() != null) {
            for (DataPoint dp : experiment.getDataPoints()) {
                dp.setExperiment(experiment);
            }
        }
        return experimentRepository.save(experiment);
    }

    @GetMapping
    public List<Experiment> getAllExperiments() {
        /*
        * +--------------------+
        * |Get the entire list.|
        * +--------------------+
        */
        return experimentRepository.findAll();
    }

    @GetMapping("/{id}")
    public Experiment getExperimentById(@PathVariable Long id) {
        /*
        * +----------+
        * |Search id.|
        * +----------+
        */
        Optional<Experiment> exp = experimentRepository.findById(id);
        return exp.orElse(null);
    }
}
