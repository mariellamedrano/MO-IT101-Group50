
import com.opencsv.CSVReader;
import java.io.FileReader;
import java.util.Scanner;
import java.time.LocalTime;

public class MotorPH {

    public static void main(String[] args) {
        String empFile = "resources/Employee Details.csv"; //csv file for employee details
        String attendanceFile = "resources/Attendance Record.csv"; //csv file for attendance record
        Scanner sc = new Scanner(System.in); //scanner for csv files

        //login first
        System.out.print("Enter username: "); //enter username
        String username = sc.nextLine();

        System.out.print("Enter password: "); // enter password
        String password = sc.nextLine();
        //Handles basic user authentication for username employee & payroll_staff login successful
        if (!username.equals("employee") && !username.equals("payroll_staff") || !password.equals("12345")) {
            System.out.println("Incorrect username and/or password.");
            return;
        }

        System.out.println("Login successful.");
        //User authentication - username is employee
        if (username.equals("employee")) {
            System.out.println("\nOptions:");
            System.out.println("1 Enter your employee number"); //option 1
            System.out.println("2 Exit the program"); //option 2
            System.out.print("Choose option: ");

            int option = sc.nextInt();
            sc.nextLine();

            if (option == 2) { //Terminate or exit the program
                System.out.println("Program terminated.");
                return;
            }

            if (option == 1) { //Enter an employee number
                System.out.print("\nEnter Employee #: ");
                String inputEmpNo = sc.nextLine();

                String empNo = "";
                String firstName = "";
                String lastName = "";
                String birthday = "";
                boolean found = false;

                try {

                    CSVReader reader = new CSVReader(new FileReader(empFile));
                    reader.readNext(); // skip header
                    String[] row;

                    while ((row = reader.readNext()) != null) {

                        if (row[0].equals(inputEmpNo)) {

                            empNo = row[0];
                            lastName = row[1];
                            firstName = row[2];
                            birthday = row[3];

                            found = true;
                            break;
                        }
                    }

                    reader.close();

                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (!found) { //if employee number is not found
                    System.out.println("Employee number does not exist.");
                    return;
                }
                // print details if employee # entered exists
                System.out.println("\nEmployee Number: " + empNo);
                System.out.println("Employee Name: " + lastName + ", " + firstName);
                System.out.println("Birthday: " + birthday);
            }
        }
        //User authentication - username is payroll_staff
        if (username.equals("payroll_staff")) {
            System.out.println("\nOptions:");
            System.out.println("1. Process Payroll"); //option 1
            System.out.println("2. Exit the program"); // option 2
            System.out.print("Choose option: ");

            int option = sc.nextInt();
            sc.nextLine();

            if (option == 2) { //Terminate or exit the program
                System.out.println("Program terminated.");
                return;
            }

            if (option == 1) { //Process Payroll
                System.out.println("\nProcess Payroll Options:");
                System.out.println("1. One employee"); //suboption 1
                System.out.println("2. All employees"); //suboption 2
                System.out.println("3. Exit the program"); //suboption 3
                System.out.print("Choose option: ");

                int subOption = sc.nextInt();
                sc.nextLine();

                if (subOption == 3) { //Terminate or exit the program
                    System.out.println("Program terminated.");
                    return;
                }

                //For Suboption 1: One Employee
                if (subOption == 1) {
                    System.out.print("\nEnter Employee #: ");
                    String inputEmpNo = sc.nextLine();
                    String empNo = "";
                    String firstName = "";
                    String lastName = "";
                    String birthday = "";
                    double hourlyRate = 0;
                    boolean found = false;

                    try {
                        CSVReader reader = new CSVReader(new FileReader(empFile));
                        reader.readNext(); // skip header
                        String[] row;

                        while ((row = reader.readNext()) != null) {
                            if (row[0].equals(inputEmpNo)) {
                                empNo = row[0];
                                lastName = row[1];
                                firstName = row[2];
                                birthday = row[3];
                                hourlyRate = Double.parseDouble(row[18]);
                                found = true;
                                break;
                            }
                        }
                        reader.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (!found) { // If employee number is not found
                        System.out.println("Employee does not exist.");
                        return;
                    }

                    // Display employee info when employee # entered exists
                    System.out.println("\n===================================");
                    System.out.println("Employee #: " + empNo);
                    System.out.println("Employee Name: " + lastName + ", " + firstName);
                    System.out.println("Birthday: " + birthday);
                    System.out.println("===================================");

                    // Hours worked calculations (June - December)
                    try {
                        CSVReader attendanceReader = new CSVReader(new FileReader(attendanceFile));
                        attendanceReader.readNext(); // skip header
                        String[] row;

                        java.time.format.DateTimeFormatter timeFormat = java.time.format.DateTimeFormatter.ofPattern("H:mm");
                        //Iterates through months to compute payroll; variables reset each month
                        for (int month = 6; month <= 12; month++) {
                            double firstHalf = 0;
                            double secondHalf = 0;

                            attendanceReader = new CSVReader(new FileReader(attendanceFile));
                            attendanceReader.readNext(); // skip header again
                            //Single-pass CSV reading would be more efficient, but here we can scan for specific month records
                            while ((row = attendanceReader.readNext()) != null) {
                                if (!row[0].equals(empNo)) {
                                    continue; // filter for this employee
                                }
                                String[] dateParts = row[3].split("/"); // MM/DD/YYYY
                                int recordMonth = Integer.parseInt(dateParts[0]);
                                int day = Integer.parseInt(dateParts[1]);
                                int year = Integer.parseInt(dateParts[2]);

                                if (recordMonth != month || year != 2024) {
                                    continue;
                                }

                                java.time.LocalTime login = java.time.LocalTime.parse(row[4].trim(), timeFormat);
                                java.time.LocalTime logout = java.time.LocalTime.parse(row[5].trim(), timeFormat);

                                double hours = computeHours(login, logout); // calculate from log in to log out

                                if (day <= 15) {
                                    firstHalf += hours; // from dates 1 - 15 first cutoff
                                } else {
                                    secondHalf += hours; // from dates 16 - second cutoff
                                }
                            }

                            String monthName = java.time.Month.of(month).name();
                            System.out.println("\nCutoff Date: " + monthName + " 1 to 15");
                            System.out.println("Total Hours Worked: " + firstHalf);

                            double grossSalary = firstHalf * hourlyRate; //computed firs cutoff hours x hourly rate read from CSV file
                            double netSalary = grossSalary; // no deductions for first cutoff so gross and net is the same
                            //Printing raw values to strictly follow the 'No Rounding' requirement, but can use printf in the future
                            System.out.println("Gross Salary: " + grossSalary);
                            System.out.println("Net Salary: " + netSalary);
                            System.out.println("(No deductions for this cutoff)");

                            System.out.println("Cutoff Date: " + monthName + " 16 to " + java.time.YearMonth.of(2024, month).lengthOfMonth());
                            System.out.println("Total Hours Worked: " + secondHalf);

                            double grossSalary2 = secondHalf * hourlyRate;
                            System.out.println("Gross Salary: " + grossSalary2);
                            //adding both cutoffs for deductions
                            double monthlyGross = grossSalary + grossSalary2;
                            System.out.println("Monthly Gross Salary: " + monthlyGross);
                            //calculate Gov deductions
                            //calculate SSS contribution
                            double sss = computeSSS(monthlyGross);
                            System.out.println("SSS Deduction: " + sss);
                            //calculate PhilHealth contribution
                            double philhealth = computePhilHealth(monthlyGross);
                            System.out.println("PhilHealth Deduction: " + philhealth);
                            //calculate Pag-ibig contribution
                            double pagibig = computePagIbig(monthlyGross);
                            System.out.println("Pag-IBIG Deduction: " + pagibig);
                            //calculate total deductions
                            double totalDeductions = sss + philhealth + pagibig;
                            System.out.println("Total Deductions: " + totalDeductions);
                            double taxableIncome = monthlyGross - totalDeductions;
                            System.out.println("Taxable Income: " + taxableIncome);
                            //calculate taxes
                            double withholdingTax = computeWithholdingTax(taxableIncome);
                            System.out.println("Withholding Tax: " + withholdingTax);
                            //calculate net salary
                            double finalNetSalary = monthlyGross - (sss + philhealth + pagibig + withholdingTax);
                            System.out.println("Final Net Salary: " + finalNetSalary);
                        }

                        attendanceReader.close();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

                // for Suboption 2: All Employees
                if (subOption == 2) {
                    try {
                        CSVReader reader = new CSVReader(new FileReader(empFile));
                        reader.readNext(); //skip the header
                        String[] row;

                        int count = 0; //counter for page break
                        int pageSize = 5; //this is the number of employees per page

                        while ((row = reader.readNext()) != null) { //looping all employees
                            String empNo = row[0];
                            String lastName = row[1];
                            String firstName = row[2];
                            String birthday = row[3];
                            double hourlyRate = Double.parseDouble(row[18]);

                            System.out.println("\n===================================");
                            System.out.println("Employee #: " + empNo);
                            System.out.println("Employee Name: " + lastName + ", " + firstName);
                            System.out.println("Birthday: " + birthday);
                            System.out.println("===================================");

                            //attendance + calculating hours worked
                            java.time.format.DateTimeFormatter timeFormat = java.time.format.DateTimeFormatter.ofPattern("H:mm");

                            for (int month = 6; month <= 12; month++) {
                                double firstHalf = 0;
                                double secondHalf = 0;

                                CSVReader attendanceReader = new CSVReader(new FileReader(attendanceFile));
                                attendanceReader.readNext(); //skip the header
                                String[] attRow;

                                while ((attRow = attendanceReader.readNext()) != null) {
                                    if (!attRow[0].equals(empNo)) {
                                        continue;
                                    }

                                    String[] dateParts = attRow[3].split("/");
                                    int recordMonth = Integer.parseInt(dateParts[0]);
                                    int day = Integer.parseInt(dateParts[1]);
                                    int year = Integer.parseInt(dateParts[2]);
                                    if (recordMonth != month || year != 2024) {
                                        continue;
                                    }

                                    java.time.LocalTime login = java.time.LocalTime.parse(attRow[4].trim(), timeFormat);
                                    java.time.LocalTime logout = java.time.LocalTime.parse(attRow[5].trim(), timeFormat);

                                    double hours = computeHours(login, logout);

                                    if (day <= 15) {
                                        firstHalf += hours;
                                    } else {
                                        secondHalf += hours;
                                    }
                                }
                                attendanceReader.close();

                                //for first cutoff
                                double grossSalary1 = firstHalf * hourlyRate;
                                double netSalary1 = grossSalary1;
                                System.out.println("\nCutoff Date: " + java.time.Month.of(month).name() + " 1 to 15");
                                System.out.println("Total Hours Worked: " + firstHalf);
                                System.out.println("Gross Salary: " + grossSalary1);
                                System.out.println("Net Salary: " + netSalary1);
                                System.out.println("(No deductions for this cutoff)");

                                //for second cutoff
                                double grossSalary2 = secondHalf * hourlyRate; //multiplying hours worked with hourly rate
                                double netSalary2 = grossSalary2;
                                double monthlyGross = grossSalary1 + grossSalary2;

                                //Gov Contributions
                                double sss = computeSSS(monthlyGross);
                                double philHealth = computePhilHealth(monthlyGross);
                                double pagibig = computePagIbig(monthlyGross);
                                double totalDeductions = sss + philHealth + pagibig;
                                // Taxable income is gross minus Gov contributions (SSS/PH/PI)
                                double taxableIncome = monthlyGross - totalDeductions;
                                // Calculating tax based on the monthly taxable income
                                double withholdingTax = computeWithholdingTax(taxableIncome);
                                // Applying all deductions and withholding tax to the second cutoff net pay
                                netSalary2 -= (totalDeductions + withholdingTax);

                                System.out.println("Cutoff Date: " + java.time.Month.of(month).name() + " 16 to " + java.time.YearMonth.of(2024, month).lengthOfMonth());
                                System.out.println("Total Hours Worked: " + secondHalf);
                                System.out.println("Gross Salary: " + grossSalary2);
                                System.out.println("Monthly Gross Salary: " + monthlyGross);
                                System.out.println("SSS Deduction: " + sss);
                                System.out.println("PhilHealth Deduction: " + philHealth);
                                System.out.println("Pag-IBIG Deduction: " + pagibig);
                                System.out.println("Total Deductions: " + totalDeductions);
                                System.out.println("Taxable Income: " + taxableIncome);
                                System.out.println("Withholding Tax: " + withholdingTax);
                                System.out.println("Final Net Salary: " + netSalary2);
                            }
                            count++;
                            //for the page view
                            if (count % pageSize == 0) {
                                System.out.println("\n--- Press Enter to continue to next employees ---");
                                sc.nextLine();
                            }
                        }
                        reader.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    //The logic is to apply a 10min grace period for late logins and 1 hour mandatory lunch
    static double computeHours(LocalTime login, LocalTime logout) {
        LocalTime workStart = LocalTime.of(8, 0);
        LocalTime graceTime = LocalTime.of(8, 10);
        LocalTime workEnd = LocalTime.of(17, 0);

        // Do not count time before 8am or after 5pm
        if (login.isBefore(workStart)) {
            login = workStart;
        }
        if (logout.isAfter(workEnd)) {
            logout = workEnd;
        }

        // If login is 8:10 or earlier, treat as 8:00 (Still considered not late)
        if (!login.isAfter(graceTime)) {
            login = workStart;
        }

        long minutesWorked = java.time.Duration.between(login, logout).toMinutes();

        // Subtract 1 hour lunch if they worked more than an hour
        if (minutesWorked > 60) {
            minutesWorked -= 60;
        } else {
            return 0.0;
        }

        double hours = minutesWorked / 60.0;
        return Math.min(hours, 8.0); //Do not include extra hours (max 8)
    }

    static double computeSSS(double salary) {
        if (salary < 3250) {
            return 135.00;
        } else if (salary <= 3750) {
            return 157.50;
        } else if (salary <= 4250) {
            return 180.00;
        } else if (salary <= 4750) {
            return 202.50;
        } else if (salary <= 5250) {
            return 225.00;
        } else if (salary <= 5750) {
            return 247.50;
        } else if (salary <= 6250) {
            return 270.00;
        } else if (salary <= 6750) {
            return 292.50;
        } else if (salary <= 7250) {
            return 315.00;
        } else if (salary <= 7750) {
            return 337.50;
        } else if (salary <= 8250) {
            return 360.00;
        } else if (salary <= 8750) {
            return 382.50;
        } else if (salary <= 9250) {
            return 405.00;
        } else if (salary <= 9750) {
            return 427.50;
        } else if (salary <= 10250) {
            return 450.00;
        } else if (salary <= 10750) {
            return 472.50;
        } else if (salary <= 11250) {
            return 495.00;
        } else if (salary <= 11750) {
            return 517.50;
        } else if (salary <= 12250) {
            return 540.00;
        } else if (salary <= 12750) {
            return 562.50;
        } else if (salary <= 13250) {
            return 585.00;
        } else if (salary <= 13750) {
            return 607.50;
        } else if (salary <= 14250) {
            return 630.00;
        } else if (salary <= 14750) {
            return 652.50;
        } else if (salary <= 15250) {
            return 675.00;
        } else if (salary <= 15750) {
            return 697.50;
        } else if (salary <= 16250) {
            return 720.00;
        } else if (salary <= 16750) {
            return 742.50;
        } else if (salary <= 17250) {
            return 765.00;
        } else if (salary <= 17750) {
            return 787.50;
        } else if (salary <= 18250) {
            return 810.00;
        } else if (salary <= 18750) {
            return 832.50;
        } else if (salary <= 19250) {
            return 855.00;
        } else if (salary <= 19750) {
            return 877.50;
        } else if (salary <= 20250) {
            return 900.00;
        } else if (salary <= 20750) {
            return 922.50;
        } else if (salary <= 21250) {
            return 945.00;
        } else if (salary <= 21750) {
            return 967.50;
        } else if (salary <= 22250) {
            return 990.00;
        } else if (salary <= 22750) {
            return 1012.50;
        } else if (salary <= 23250) {
            return 1035.00;
        } else if (salary <= 23750) {
            return 1057.50;
        } else if (salary <= 24250) {
            return 1080.00;
        } else if (salary <= 24750) {
            return 1102.50;
        } else {
            return 1125.00;
        }

    }
    //Computes the PhilHealth Contribution
    static double computePhilHealth(double salary) {

        double premium;

        if (salary >= 60000) {
            premium = 1800;
        } else {
            premium = salary * 0.03;
        }

        double employeeShare = premium / 2;

        return employeeShare;
    }
    //Implements a capped contribution (max of 100 pesos for salaries above 1,500 pesos)
    static double computePagIbig(double salary) {

        double contribution;

        if (salary <= 1500) {
            contribution = salary * 0.01;
        } else {
            contribution = salary * 0.02;
        }
        if (contribution > 100) {
            contribution = 100;
        }
        return contribution;
    }
    //Monthly withholding tax based on the tax matrix provided by MotorPH
    static double computeWithholdingTax(double taxableIncome) {

        double tax = 0;

        if (taxableIncome <= 20832) {
            tax = 0;
        } else if (taxableIncome < 33333) {
            tax = (taxableIncome - 20833) * 0.20;
        } else if (taxableIncome < 66667) {
            tax = 2500 + (taxableIncome - 33333) * 0.25;
        } else if (taxableIncome < 166667) {
            tax = 10833 + (taxableIncome - 66667) * 0.30;
        } else if (taxableIncome < 666667) {
            tax = 40833.33 + (taxableIncome - 166667) * 0.32;
        } else {
            tax = 200833.33 + (taxableIncome - 666667) * 0.35;
        }

        return tax;
    }
}
