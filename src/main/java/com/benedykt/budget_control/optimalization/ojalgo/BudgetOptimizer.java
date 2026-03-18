package com.benedykt.budget_control.optimalization.ojalgo;

import org.ojalgo.optimisation.ExpressionsBasedModel;

public class BudgetOptimizer {

    private double defMaxPercentOfHousing = 0.35;
    private double defMaxPercentOfFoodDrinks = 0.20;
    private double defMaxPercentOfTransport = 0.12;
    private double defMaxPercentOfShopping = 0.10;
    private double defMaxPercentOfEntertainment = 0.08;
    private double defMaxPercentOfPets = 0.05;
    private double defMaxPercentOfOther = 0.1;

    private double minExpenseOnCategory = 0;

    private double minExpenseOfHousing = 0;
    private double minExpenseOfFoodDrinks = 0;
    private double minExpenseOfTransport = 0;
    private double minExpenseOfShopping = 0;
    private double minExpenseOfEntertainment = 0;
    private double minExpenseOfPets = 0;
    private double minExpenseOfOther = 0;

    public void BudgetOptimizerWithoutPaymentHistory() {

//        BasicLogger.debug("BudgetOptimizerWithoutPaymentHistory()");

        double income = 10000;
        double savingPercent = 0.2;
        double savingAmount = income - savingPercent * income;



        double maxExpenseOfHousing = savingAmount * 0.35;
        double maxExpenseOfFoodDrinks = savingAmount * 0.20;
        double maxExpensePercentOfTransport = savingAmount * 0.12;
        double maxExpensePercentOfShopping = savingAmount * 0.10;
        double maxExpensePercentOfEntertainment = savingAmount * 0.08;
        double maxExpensePercentOfPets = savingAmount * 0.05;
        double maxExpensePercentOfOther = savingAmount * 0.1;

        ExpressionsBasedModel model = new ExpressionsBasedModel();




    }

}
