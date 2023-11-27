package HospitalManagementSystem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

public class HospitalManagement {
    private static final String url = "jdbc:mysql://localhost:3306/hospital";
    private static final String username = "root";
    private static final String password = "myselfmysql123";

    public static void main(String[] args) {
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
        }catch(ClassNotFoundException e) {
            e.printStackTrace();
        }
         Scanner scanner = new Scanner(System.in);
        try{
            Connection connection = DriverManager.getConnection(url, username, password);
            Patient patient = new Patient(connection, scanner);
            Doctor doctor = new Doctor(connection);

            while(true){
                System.out.println("HOSPITAL MANAGEMENT SYSTEM");
                System.out.println("1. Add Patient");
                System.out.println("2. View Patients");
                System.out.println("3. View Doctors");
                System.out.println("4. Book Appointments");
                System.out.println("5. Exit");
                System.out.println("Enter your choice : ");
                switch(scanner.nextInt()){
                    case 1: //Add Patient
                        patient.addPatient();
                        System.out.println();
                        break;

                    case 2: //View Patients
                        patient.viewPatients();
                        System.out.println();
                        break;

                    case 3: //View Doctors
                        doctor.viewDoctors();
                        System.out.println();
                        break;

                    case 4: //Book Appointments
                        bookAppointment(patient, doctor, connection, scanner);
                        System.out.println();
                        break;

                    case 5: //Exit
                          return;

                    default:
                        System.out.println("Enter valid choice!!!");
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    //method to book appointment
    public static void bookAppointment(Patient patient, Doctor doctor, Connection connection, Scanner scanner) {
        System.out.println("Enter Patient id : ");
        int patientId = scanner.nextInt();
        System.out.println("Enter Doctor id : ");
        int doctorId = scanner.nextInt();
        System.out.println("Enter Appointment date (YYYY-MM-DD)");
        String appointmentDate = scanner.next();

        //check if the patient and doctor both exists --> by patient id and doctor id
        if(patient.getPatientById(patientId) && doctor.getDoctorById(doctorId)){
            //check if doctor is available on that particular date --> by doctor id and appointment date
            if(checkDoctorAvailability(doctorId, appointmentDate, connection)){
                String appointmentQuery = "INSERT INTO appointments(patient_id, doctor_id, appointment_date) VALUES (?, ?, ?)";
                try{
                    PreparedStatement preparedStatement = connection.prepareStatement(appointmentQuery);
                    preparedStatement.setInt(1, patientId);
                    preparedStatement.setInt(2, doctorId);
                    preparedStatement.setString(3, appointmentDate);
                    //variable to check if any row is affested or not--> while inserting values to the database
                    int affectedRows = preparedStatement.executeUpdate();
                    if(affectedRows>0){
                        System.out.println("Appointment Booked!!!");
                    }else{
                        System.out.println("Failed to book appointment!!!");
                    }
                }catch(Exception e)
                {
                    e.printStackTrace();
                }
            }else{
                System.out.println("Doctor not available on this date!!");
            }
        }else{
            System.out.println("Either doctor or patient does not exist!");
        }
    }
    //method to check if doctor is available on that date
    public static boolean checkDoctorAvailability(int doctorId, String appointmentDate, Connection connection){
        String query = "SELECT COUNT(*) FROM appointments WHERE doctor_id = ? AND appointment_date = ?";
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, doctorId);
            preparedStatement.setString(2, appointmentDate);
            ResultSet resultset = preparedStatement.executeQuery();
            if(resultset.next()){
                int count = resultset.getInt(1);
                //if there is no row for that particular doctor id and appointment date, that means doctor is available
                if(count == 0){
                    return true;
                }else{
                    return false;
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }
}
