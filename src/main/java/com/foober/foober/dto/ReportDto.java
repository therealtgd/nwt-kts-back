package com.foober.foober.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;

@Data
@AllArgsConstructor
public class ReportDto {
    private int sumOfRides;
    private double sumOfDistance;
    private double sumOfTransactions;
    private double averageRides;
    private double averageDistance;
    private double averageTransactions;
    private ArrayList<Integer> ridesData;
    private ArrayList<Double> distanceData;
    private ArrayList<Double> transactionsData;
    private ArrayList<String> labels;
}
