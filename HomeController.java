package io.corona.coronavirustracker.controllers;

import io.corona.coronavirustracker.models.LocationStats;
import io.corona.coronavirustracker.services.CoronaDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller  //not a REST controller, bcz it has to render HTML UI
public class HomeController {

    @Autowired // autowired dataSERVICE to controller to get the data that is retrived from the LIST
    CoronaDataService coronaDataService;

    @GetMapping("/")
    public String home(Model model)
    {
        List<LocationStats> allStats   = coronaDataService.getAllstats();
        /*taking List of objects, converting to stream and mapping it to INTEGER, each object maps to
        * integer value  which i totla case for that record then SUM it all*/
        int totalReportedCases= allStats.stream().mapToInt(stat->stat.getLatestTotalCases()).sum();
        int totalNewCases= allStats.stream().mapToInt(stat->stat.getDiffFromPrevDay()).sum();

        allStats.stream().forEach(state_var-> System.out.println(state_var.getState()));


        model.addAttribute("allStats",allStats); //getAllstats() is getter in Service
        model.addAttribute("totalReportedCases",totalReportedCases);
        model.addAttribute("totalNewCases",totalNewCases);
            return "home";  // this should map to home.html

    }
}
