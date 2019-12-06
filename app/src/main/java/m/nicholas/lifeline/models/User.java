package m.nicholas.lifeline.models;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import m.nicholas.lifeline.Constants;

public class User {
    private String name;
    private String dateOfBirth;
    private int age;
    private int weight;
    private int height;
    private String bloodType;
    private List<String> allergies = new ArrayList<>();
    private List<String> medications = new ArrayList<>();
    private String notes;

    /*-- Empty constructor required by Firebase --*/
    public User(){}

    public User(String name, String dateOfBirth) {
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        height = 0;
        weight = 0;
        this.bloodType = Constants.NOT_SPECIFIED;
        this.allergies.add("None");
        this.medications .add("None");
        notes = "None";
        calculateAge(dateOfBirth);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getBloodType() {
        return bloodType;
    }

    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
    }

    public List<String> getAllergies() {
        return allergies;
    }

    public void setAllergies(List<String> allergies) {
        this.allergies = allergies;
    }

    public List<String> getMedications() {
        return medications;
    }

    public void setMedications(List<String> medications) {
        this.medications = medications;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    private void calculateAge(String dateOfBirth){
        String[] dobArr = dateOfBirth.split("-");
        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();
        dob.set(Integer.parseInt(dobArr[2]), Integer.parseInt(dobArr[1]), Integer.parseInt(dobArr[0]));

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)){
            age--;
        }
        setAge(age);
    }
}
