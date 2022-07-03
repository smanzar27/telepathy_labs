package com.telepathy.labs.assignment;

import com.telepathy.labs.exception.CostException;
import com.telepathy.labs.exception.UserInputException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class QuestionTwo {

    public static List<String> getPlanDetails(String planDetails) {

        return  Pattern.compile(",")
                .splitAsStream(planDetails)
                .collect(Collectors.toList());
    }
    public static Map<String, Integer> sortedCostPlanMap(Map<String, Integer> unSortedMap){

        LinkedHashMap<String, Integer> sortedMap = new LinkedHashMap<>();
        unSortedMap.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .forEachOrdered(x -> sortedMap.put(x.getKey(), x.getValue()));
        return sortedMap;
    }
    public static Map<String, Integer> getCostPlanMapOfSingleService(String fileName, Set<String> plans) {

        Map<String, Integer> plansCostMap = new HashMap<>();
        plans.forEach(plan -> {
            try {
                plansCostMap.put(plan,getCostByPlan(fileName,plan));
            } catch (UserInputException e) {
                throw new RuntimeException(e);
            }
        });
        return plansCostMap;
    }

    public static Map<String, Integer> getCostPlanMapOfMultipleService(String fileName, Set<String> plans) {

        System.out.println(plans);
        Map<String, Integer> plansCostMap = new HashMap<>();
        plans.forEach(plan -> {
            List<String> subPlans = Arrays.asList(plan.split(","));
            AtomicInteger subPlanCost = new AtomicInteger();
            subPlans.forEach(subPlan -> {
                try {
                    subPlanCost.set(subPlanCost.get() + getCostByPlan(fileName, subPlan));
                } catch (UserInputException e) {
                    throw new RuntimeException(e);
                }
            });
            plansCostMap.put(plan,subPlanCost.get());
        });
        return plansCostMap;
    }
    public static Integer getCostByPlan(String fileName, String plan) throws UserInputException {

        String strLine;
        String filePath = System.getProperty("user.dir") + File.separator + fileName;
        Integer planCost = null;
        try {

            File file = new File(filePath);
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);

            while ((strLine = br.readLine()) != null) {
                if(strLine.contains(plan)) {
                    planCost =Integer.parseInt(getPlanDetails(strLine).get(1));
                    if(planCost <=0 )
                        throw new CostException("SERVICE PLAN COST CANNOT BE ZERO OR LESS");
                }
            }
        }   catch(IOException ex) {
                throw new UserInputException("FILE NOT FOUND OR FILE NOT READABLE");
        }   catch(Exception ex) {
                ex.printStackTrace();
        }
        return planCost;
    }
    public static Set<String> getPlansByService(String fileName, String service) throws UserInputException {

        String strLine;
        String filePath = System.getProperty("user.dir") + File.separator + fileName;
        Set<String> servicesSet = new HashSet<>();

        try {

            File file = new File(filePath);
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);

            while ((strLine = br.readLine()) != null) {
               if(strLine.contains(service)) {
                   List<String> servicePlan = getPlanDetails(strLine);
                   servicesSet.add(servicePlan.get(0));
               }
            }
        }   catch(IOException ex) {
            throw new UserInputException("FILE NOT FOUND OR FILE NOT READABLE");
        }   catch(Exception ex) {
            ex.printStackTrace();
        }

        servicesSet=sortedHashSet(servicesSet);
        return servicesSet;
    }
    public static Set<String> sortedHashSet(Set<String> services){

        List<String> servicesList = new ArrayList<>(services);
        Collections.sort(servicesList);
        return new LinkedHashSet<>(servicesList);
    }
    public static Map<String,Set<String>> getRequiredServices(String fileName, List<String> services){

        Map<String,Set<String>> requiredServicesMap = new HashMap<>();
        services.forEach(service -> {
            try {

                Set<String> servicesPlan = getPlansByService(fileName,service);
                if(!servicesPlan.isEmpty())
                    requiredServicesMap.put(service,servicesPlan);
            } catch (UserInputException e) {
                throw new RuntimeException(e);
            }
        });
        return requiredServicesMap;
    }
    public static Set<String> convertStringToSet(String strElement) {

        List<String> hashSet = Arrays.asList(strElement.split(","));
        return new HashSet<>(hashSet);
    }
    public static Set<String> combineServicePlans(Set<String> firstPlans, Set<String> SecondPlans){

        Set<String> combineServicePlans = new HashSet<>();
        firstPlans
            .forEach(firstSetElement -> SecondPlans
                .forEach(secondSetElement -> {
                    Set<String> commonServicePlans = new HashSet<>(convertStringToSet(firstSetElement));
                    commonServicePlans.add(secondSetElement);
                    commonServicePlans=sortedHashSet(commonServicePlans);
                    combineServicePlans.add(String.join(",", commonServicePlans));
                })
            );
       return combineServicePlans;
    }

    public static void main(String[] args) throws UserInputException {

        List<String> requiredServices;
        Set<String> servicePlansFirst;
        Set<String> servicePlansToCombine;
        Set<String> servicePlansCombined = null;
        Map<String, Set<String>> requiredServicesMapTable;
        Map<String, Integer> plansCostMap;

        try {

            String fileName = args[0];
            String requiredService = args[1];

            requiredServices = Arrays.asList(requiredService.split(","));
            requiredServices = new ArrayList<>(new HashSet<>(requiredServices));
            requiredServicesMapTable=getRequiredServices(fileName,requiredServices);

            int planSize = requiredServicesMapTable.size();
            if(planSize == 0)
                System.out.println(0);

            else if(planSize == 1) {

                System.out.println(requiredServicesMapTable);
                Optional<String> firstKey = requiredServicesMapTable.keySet().stream().findFirst();
                String serviceName = firstKey.get();
                plansCostMap=getCostPlanMapOfSingleService(fileName,requiredServicesMapTable.get(serviceName));
                plansCostMap=sortedCostPlanMap(plansCostMap);
                firstKey = plansCostMap.keySet().stream().findFirst();
                System.out.println(plansCostMap.get(firstKey.get()) + "," + firstKey.get());

            } else {

                System.out.println(requiredServicesMapTable);
                Optional<String> firstKey = requiredServicesMapTable.keySet().stream().findFirst();
                String serviceName= firstKey.get();
                servicePlansFirst=requiredServicesMapTable.get(serviceName);

                int count =0;
                for (Map.Entry<String, Set<String>> stringSetEntry : requiredServicesMapTable.entrySet()) {
                    count++;
                    if (count == 2) {
                        servicePlansToCombine = stringSetEntry.getValue();
                        servicePlansCombined = combineServicePlans(servicePlansFirst, servicePlansToCombine);
                    }
                    if (count > 2) {
                        servicePlansToCombine = stringSetEntry.getValue();
                        servicePlansCombined = combineServicePlans(servicePlansCombined, servicePlansToCombine);
                    }
                }
                plansCostMap=getCostPlanMapOfMultipleService(fileName,servicePlansCombined);
                plansCostMap=sortedCostPlanMap(plansCostMap);
                firstKey = plansCostMap.keySet().stream().findFirst();
                System.out.println(plansCostMap.get(firstKey.get()) + "," + firstKey.get());
            }

        }   catch(ArrayIndexOutOfBoundsException ex) {
                throw new UserInputException("EITHER FILE OR SERVICE OR BOTH PARAMETER MISSING");
        }   catch(Exception ex) {
                ex.printStackTrace();
        }
    }
}