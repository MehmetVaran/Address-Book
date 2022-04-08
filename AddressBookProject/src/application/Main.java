package application;
	
import java.io.IOException;
import java.io.RandomAccessFile;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;


public class Main extends Application {
	
	int index = 0, counter = 0;
	Person[] addressBookArray;
	
	
	final static int ID_SIZE = 4;
	final static int NAME_SIZE = 32;
	final static int STREET_SIZE = 32;
	final static int CITY_SIZE = 20;
	final static int GENDER_SIZE = 1;
	final static int ZIP_SIZE = 5;
	final static int RECORD_SIZE = (ID_SIZE + NAME_SIZE + STREET_SIZE + CITY_SIZE + GENDER_SIZE + ZIP_SIZE);
	public RandomAccessFile raf;
	
	TextField tfID = new TextField();
	TextField tfSearchID = new TextField();
	TextField tfName = new TextField();
	TextField tfStreet = new TextField();
	TextField tfCity = new TextField();
	TextField tfGender = new TextField();
	TextField tfZip = new TextField();
	
	Button btAdd = new Button("Add");
	Button btFirst = new Button("First");
	Button btNext = new Button("Next");
	Button btPrevious = new Button("Previous");
	Button btLast = new Button("Last");
	Button btUpdateByID = new Button("Update By ID");
	Button btSearchByID = new Button("Search By ID");
	Button btClear = new Button("Clear Text Fields");
	
	Label lbID = new Label("ID");
	Label lbSearchID = new Label("Search/Update ID");
	Label lbName = new Label("Name");
	Label lbStreet = new Label("Street");
	Label lbCity = new Label("City");
	Label lbGender = new Label("Gender");
	Label lbZip = new Label("Zip");
	
	public Main () {
		
		try {
			raf = new RandomAccessFile("address.dat" , "rw");
			addressBookArray = new Person[100];
		}
		catch (IOException ex) {
			ex.printStackTrace();
			System.exit(1); 
		}
	}

	@Override
	public void start(Stage primaryStage) {
		try {
			tfID.setPrefColumnCount(4);
			tfID.setDisable(true); 
			tfGender.setPrefColumnCount(1);
			tfZip.setPrefColumnCount(4);
			tfCity.setPrefColumnCount(12);
			
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Information Dialog");
			alert.setHeaderText("Look, an Information Dialog");
			
			GridPane p1 = new GridPane();
			p1.setAlignment(Pos.CENTER);
			p1.setHgap(5);
			p1.setVgap(5);
			
			p1.add(lbID, 0, 0);
		
			p1.add(lbName, 0, 1);
			p1.add(tfName, 1, 1);
			
			p1.add(lbStreet, 0, 2);
			p1.add(tfStreet, 1, 2);
			
			p1.add(lbCity, 0, 3);
			
			HBox p2 = new HBox(5);
			p2.getChildren().addAll(tfCity, lbGender, tfGender, lbZip, tfZip);
			p1.add(p2, 1, 3);
			
			HBox p3 = new HBox(5);
			p3.getChildren().addAll(btAdd, btFirst, btNext, btPrevious, btLast, btUpdateByID, btSearchByID,  btClear);
			p3.setAlignment(Pos.CENTER);
			
			HBox p4 = new HBox(5);
			p4.getChildren().addAll(tfID, lbSearchID, tfSearchID);
			p1.add(p4, 1, 0);
			
			
			BorderPane borderPane = new BorderPane();
			borderPane.setCenter(p1);
			borderPane.setBottom(p3);
			
			Scene scene = new Scene(borderPane, 600, 200);
			primaryStage.setTitle("Address Book App");
			primaryStage.setScene(scene);
			primaryStage.show();
			
			try {
				
				if (raf.length() > 0 ) {
					long currentPos = raf.getFilePointer();
					while (currentPos < raf.length()) {
						readFileFillArray(addressBookArray, currentPos);
						currentPos = raf.getFilePointer();
					}
					readFileByPos(0);
				}
			}
			catch (IOException ex) {
				ex.printStackTrace();
				}
			
			btAdd.setOnAction(e->{
				int nullAddFlag = 0;
				int number = 0;
				int sameIDFlag = 1;
				
				if (nullChecker() == 0) {
					alert.setContentText("Some parts are empty!");
					alert.showAndWait();
					nullAddFlag = 0;
				}
				else {
					nullAddFlag = 1;
				}
				
				if(nullAddFlag == 1) {
					try {
						if (index == 0) {
							writeAddressToFile(raf.length());
							readFileFillArray(addressBookArray, RECORD_SIZE*2*(index));
							alert.setContentText("Record is added successfully!");
							alert.showAndWait();
							clearTextFields();
							}
					
						else {
							for (int idChecker = 0; idChecker < index; idChecker++) {
								number = Integer.valueOf(tfSearchID.getText());
								if (number == addressBookArray[idChecker].getId()) {
									alert.setContentText("There is 1 log with same ID number!");
									alert.showAndWait();
									break;
								}
								else {
									sameIDFlag = 0;
								}
							}

						}
						
						if (sameIDFlag == 0) {
							writeAddressToFile(raf.length());
							readFileFillArray(addressBookArray, RECORD_SIZE*2*(index));
							alert.setContentText("Record is added successfully!");
							alert.showAndWait();
							clearTextFields();
						}
						
					}
					catch (Exception ex) {
						
					}
				}
				
			}); 
			
			btFirst.setOnAction(e->{
				counter = 0;
				if (addressBookArray[counter] != null) {
					printFromArray();
				}
				else {
					alert.setContentText("Address Book is empty!");
					alert.showAndWait();
				}
			});
			
			btNext.setOnAction(e->{
				counter++;
				if (addressBookArray[counter] != null) {
					printFromArray();
				}
				else {
					alert.setContentText("Next log is empty!");
					alert.showAndWait();
				}
			});
			
			btPrevious.setOnAction(e->{
				if(counter <= 0) {
					alert.setContentText("There is no previous log!");
					alert.showAndWait();
				}
				else {
					counter--;
					printFromArray();
				}
			});
			
			btLast.setOnAction(e->{
				while (addressBookArray[counter] != null) {
					counter++;
				}
				counter--;
				printFromArray();				
			});
			
			btUpdateByID.setOnAction(e->{
				int idChecker = 0;
				int nullUpdateFlag = 0;
				int updatedFlag = 0;
				int number = 0;
				
				if (nullChecker() == 0) {
					alert.setContentText("Some parts are empty!");
					alert.showAndWait();
					nullUpdateFlag = 0;
				}
				else {
					nullUpdateFlag = 1;
				}
				
				if (nullUpdateFlag == 1) {
					number = Integer.valueOf(tfSearchID.getText());
					while (idChecker < index) {
						if (number == addressBookArray[idChecker].getId()) {
							writeAddressToFile(RECORD_SIZE*2*(idChecker));
							updateByIDArray(idChecker);
							alert.setContentText("Log is updated!");
							alert.showAndWait();
							updatedFlag = 1;
							break;
						}
						else {
							idChecker++;
						}
					}
					if (updatedFlag == 0) {
						alert.setContentText("ID is invalid!");
						alert.showAndWait();
					}
				}
			});
			
			btSearchByID.setOnAction(e->{
				int idChecker = 0;
				int flag = 0;
				int number = Integer.valueOf(tfSearchID.getText());
				
				while (idChecker < index) {
					if(number == addressBookArray[idChecker].getId()) {
						counter = idChecker;
						printFromArray();
						flag = 0;
						break;
					}
					else {
						idChecker++;
						flag = 1;
					}
				}
				
				if (flag == 1) {
					alert.setContentText("There is no log!");
					alert.showAndWait();
				}
				
				
			});
				
			btClear.setOnAction(e->{
				clearTextFields();			
				
			});
		
		}
	
			catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public int nullChecker() {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Information Dialog");
		alert.setHeaderText("Look, an Information Dialog");
		int x = 0;
		int intCheck = 0;
		
		try {
			intCheck = Integer.valueOf(tfSearchID.getText());
		}
		catch (Exception e) {
			alert.setContentText("Search ID must be integer!");
			alert.showAndWait();
		}
		if (tfSearchID.getText().isEmpty() == true || tfName.getText().isEmpty() == true || tfStreet.getText().isEmpty() == true || tfCity.getText().isEmpty() == true || tfGender.getText().isEmpty() == true || tfZip.getText().isEmpty() == true) {
			return x;
		}
		else {
			return x+1;
		}
	}
	
	public void updateByIDArray(int x) {
		addressBookArray[x].setName(tfName.getText());
		addressBookArray[x].setStreet(tfStreet.getText());
		addressBookArray[x].setCity(tfCity.getText());
		addressBookArray[x].setGender(tfGender.getText());
		addressBookArray[x].setZip(tfZip.getText());
	}
	
	public void printFromArray() {
		String IDstring = String.valueOf(addressBookArray[counter].getId());
		tfID.setText(IDstring);
		tfName.setText(addressBookArray[counter].getName());
		tfStreet.setText(addressBookArray[counter].getStreet());
		tfCity.setText(addressBookArray[counter].getCity());
		tfGender.setText(addressBookArray[counter].getGender());
		tfZip.setText(addressBookArray[counter].getZip());	
	}
	
	public void readFileFillArray(Person[]people, long position) throws IOException {
		raf.seek(position);
		String id = FileOperations.readFixedLengthString(ID_SIZE, raf);
		int intID = Integer.parseInt(id.trim().toString());
		String name = FileOperations.readFixedLengthString(NAME_SIZE, raf).trim();
		String street = FileOperations.readFixedLengthString(STREET_SIZE, raf).trim();
		String city = FileOperations.readFixedLengthString(CITY_SIZE, raf).trim();
		String gender = FileOperations.readFixedLengthString(GENDER_SIZE, raf).trim();
		String zip = FileOperations.readFixedLengthString(ZIP_SIZE, raf).trim();
		
		Person p = new Person(intID, name, gender, street, city, zip);
		people[index] = p;
		index++;
		
	}
	
	public void readFileByPos(long position) throws IOException {
		raf.seek(position);
		String id = FileOperations.readFixedLengthString(ID_SIZE, raf);
		String name = FileOperations.readFixedLengthString(NAME_SIZE, raf);
		String street = FileOperations.readFixedLengthString(STREET_SIZE, raf);
		String city = FileOperations.readFixedLengthString(CITY_SIZE, raf);
		String gender = FileOperations.readFixedLengthString(GENDER_SIZE, raf);
		String zip = FileOperations.readFixedLengthString(ZIP_SIZE, raf);
		
		tfID.setText(id);
		tfName.setText(name);
		tfStreet.setText(street);
		tfCity.setText(city);
		tfGender.setText(gender);
		tfZip.setText(zip);
		
	}
	
	public void clearTextFields() {
		tfID.clear();
		tfSearchID.clear();
		tfName.clear();
		tfStreet.clear();
		tfCity.clear();
		tfGender.clear();
		tfZip.clear();
	}
	
	public void writeAddressToFile(long position) {
		try {
			raf.seek(position);
			FileOperations.writeFixedLengthString(tfSearchID.getText(), ID_SIZE, raf);
			FileOperations.writeFixedLengthString(tfName.getText(), NAME_SIZE, raf);
			FileOperations.writeFixedLengthString(tfStreet.getText(), STREET_SIZE, raf);
			FileOperations.writeFixedLengthString(tfCity.getText(), CITY_SIZE, raf);
			FileOperations.writeFixedLengthString(tfGender.getText(), GENDER_SIZE, raf);
			FileOperations.writeFixedLengthString(tfZip.getText(), ZIP_SIZE, raf);
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	
	public static void main(String[] args) {
		launch(args);
	}
}
