MS2 - MotorPH Basic Payroll Program

Project Overview: 
This Java application is a procedural basic payroll management that automates salary calculations by integrating attendance data with provided government matrices (SSS, PhilHealth, Pag-Ibig, and Withholding tax).

Logic:
- User authentication based on user access (employee vs. payroll staff)
- The system utilizes OpenCSV to parse external employee records and attendance logs.
- Jar files added for parsing
- Hours worked are calculated based on login/logout times with a 10-minute grace period for lates and a 1 hout lunch break deduction
- No deductions for first cutoff, dates 1 - 15, gross is equals to net salary
- 1st and 2nd gross are summed before computing deductions to ensure employee is placed in the correct SSS/PhilHealth brackets.
- Deductions applied to second cutoff, dates 16 - end of the month
- Withholding tax is calculated based on the given Tax matrix after all Gov contributions are subtracted
- No rounding rule, the system enforces a no rounding policy, all values are displayed as raw values

Flow:
- Authentication
- Input selection, option > suboptions
- Hours calculation based on user access
- Monthly Consolidation, total gross salaries for both cutoffs
- Gov Deductions including the tax computation
- Final Reporting, detailed breakdown of take-home pay, showing months June to December's calculations per employee
- For all employees, a page break/pagination was added to show 5 employees per page, hit enter to go to next page

How to Run (for testers)
This program is built using Java with Ant in Netbeans
1. Clone/Download the repository as a ZIP and extract it.
2. In Netbeans, go to File > Open Project and select the extracted folder.
3. The required JAR files are included in the lib folder. If you see an error, right-click the Libraries folder > Properties > Add JAR/Folder and select the files inside the lob directory.
4. Ensure the resources folder containing the CSV files is in the project root.
5. Right-click MotorPH.java and select Run File.
