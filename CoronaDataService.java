package io.corona.coronavirustracker.services;

import io.corona.coronavirustracker.models.LocationStats;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;


@Service
public class CoronaDataService {

    //below variable contains the virus data GITHUB URL
   private static String VIRUS_DATA_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";

    private List<LocationStats> allstats = new ArrayList<>();

    public List<LocationStats> getAllstats() {
        return allstats;
    }

    @PostConstruct  //means , telling spring when u constract instance of above serice, execute below method
@Scheduled(cron = "* * 1 * * *") //runs a method on regular basis , *(seconds,min,hour,day,month,year)
 // * * 1 * * * means it will run 1st hour of every day , 3rd * denotes hour,,,,update everyday
    public void fetchVirusData() throws IOException,InterruptedException
    {
        //this will do the http call to the github URL, using HTTP client
         List<LocationStats> newStats = new ArrayList<>();


        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()  //creating a new request
            .uri(URI.create(VIRUS_DATA_URL))
                .build();
        HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
        //stringreader is instance of reader which parse a striing
        StringReader csvBodyReader = new StringReader(httpResponse.body());
        /*taken from http://commons.apache.org/proper/commons-csv/user-guide.html
         Header auto detection Header auto detection
         to detect the header of each columns to organise the data display*/
        //***************************to parse the data*********************************************
        Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(csvBodyReader); //Reader instance is to read text
        for (CSVRecord record : records) {
            LocationStats locationStat = new LocationStats();

            locationStat.setState(record.get("Province/State"));
            locationStat.setCountry(record.get("Country/Region"));
            int latestCases = Integer.parseInt(record.get(record.size()-1));
            int prevDayCases= Integer.parseInt(record.get(record.size()-2));

            locationStat.setLatestTotalCases(latestCases); //size gives lenght of header
            locationStat.setDiffFromPrevDay(latestCases-prevDayCases);
            System.out.println(locationStat);

            newStats.add(locationStat);
//            String customerNo = record.get("CustomerNo");
//            String name = record.get("Name");
        }
        this.allstats = newStats;

        //***********************************************************************



    }






}

