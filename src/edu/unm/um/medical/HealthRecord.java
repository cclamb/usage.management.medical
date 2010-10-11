package edu.unm.um.medical;
//package medTrading.buyer;


public class HealthRecord  {
   private String medCondition;
   
   private String status;
   private String medicine;
   private int dosageinmg;
   private int usagetimesdaily;

   public String getMedCondition() {
      return medCondition;
   }

   
   public String getStatus() {
      return status;
   }

   public String getMedicine() {
      return medicine;
   }

   public int getDosageInMg() {
      return dosageinmg;
   }
 
   public int getUsageTimesDaily() {
      return usagetimesdaily;
   }

   public void setMedCondition(String medCondition) {
      this.medCondition = medCondition;
   }

   

   public void setStatus(String status) {
      this.status = status;
   }

   public void setMedicine(String medicine) {
      this.medicine = medicine;
   }

   public void setDosageInMg(int dosageinmg) {
      this.dosageinmg = dosageinmg;
   }

   public void setUsageTimesDaily(int usagetimesdaily) {
       this. usagetimesdaily = usagetimesdaily;
   }
}
