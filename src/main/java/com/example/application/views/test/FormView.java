package com.example.application.views.test;

import com.example.application.data.entity.Car;
import com.example.application.data.entity.Manufacturer;
import com.example.application.data.entity.Person;
import com.example.application.data.service.InsuranceService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.accordion.AccordionPanel;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.listbox.MultiSelectListBox;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@PageTitle("Transportlīdzekļa apdrošināšana")
@Route(value = "", layout = MainLayout.class)
public class FormView extends Div {

    private final InsuranceService insuranceService;
    private final Binder<Car> carBinder;
    private final Binder<Person> personBinder;
    private final Accordion accordion;
    private Car savedCar;
    private Person savedPerson;

    private final MultiSelectListBox<String> insurance;

    public FormView(InsuranceService insuranceService) {

        this.insuranceService = insuranceService;

        this.setWidth("auto");

        Image image = new Image("images/car3.png", "");
        image.setMaxWidth("650px");
        image.setMaxHeight("500px");

        Image image2 = new Image("images/driver2.png", "");
        image2.setMaxWidth("650px");
        image2.setMaxHeight("500px");

        String carInfo = "Transportlīdzekļa informācija";

        String personInfo = "Īpašnieka kontaktinformācija";

        accordion = new Accordion();

        carBinder = new Binder<>(Car.class);
        carBinder.setBean(new Car());

        personBinder = new Binder<>(Person.class);
        personBinder.setBean(new Person());

        HorizontalLayout carHorizontalLayout = new HorizontalLayout();
        carHorizontalLayout.setMargin(true);
        carHorizontalLayout.setPadding(true);
        VerticalLayout carVerticalLayout = new VerticalLayout();

        HorizontalLayout personHorizontalLayout = new HorizontalLayout();
        personHorizontalLayout.setMargin(true);
        personHorizontalLayout.setPadding(true);
        VerticalLayout personVerticalLayout = new VerticalLayout();

        AccordionPanel carFormPanel = accordion.add(carInfo, carHorizontalLayout);

        AccordionPanel personFormPanel = accordion.add(personInfo, personHorizontalLayout);
        personFormPanel.setEnabled(false);

        Select<String> manufacturer = new Select<>();
        manufacturer.setLabel("Izstrādātājs");
        manufacturer.setWidth("300px");
        carBinder.forField(manufacturer).bind("manufacturer");

        List<String> manufacturersList = this.insuranceService.getManufacturers()
                .stream().map(Manufacturer::getManufacturerName)
                .sorted().collect(Collectors.toList());
        manufacturer.setItems(manufacturersList);

        TextField model = new TextField("Auto modelis");
        model.setWidth("300px");
        model.setRequiredIndicatorVisible(true);
        model.setErrorMessage("Nepieciešams lauks");
        carBinder.forField(model).bind("model");

        TextField registrationLicenseNo = new TextField("Reģistrācijas Apliecības Nr.");
        registrationLicenseNo.setWidth("300px");
        registrationLicenseNo.setRequiredIndicatorVisible(true);
        registrationLicenseNo.setErrorMessage("Nepieciešams lauks");
        carBinder.forField(registrationLicenseNo).bind("registrationLicenseNo");


        TextField stateRegNo = new TextField("Valsts reģistrācijas Nr.");
        stateRegNo.setWidth("300px");
        carBinder.forField(stateRegNo).bind("stateRegNo");

        Select<Integer> yearProduced = new Select<>();
        yearProduced.setWidth("300px");
        yearProduced.setLabel("Izlaiduma gads");
        carBinder.forField(yearProduced).bind("yearProduced");

        yearProduced.setItems(getCarYearValues());

        insurance = new MultiSelectListBox<>();
        insurance.setItems("OCTA", "KASKO");
        insurance.setWidth("300px");

        carVerticalLayout.add(manufacturer, model, registrationLicenseNo, stateRegNo, yearProduced, insurance);
        carHorizontalLayout.add(carVerticalLayout, image);

        Button carDetailsOk = new Button("Ok", (e) -> {
            if (!validCarInputs()) {
                return;
            }
            saveInputCar();
            carFormPanel.setEnabled(false);
            carFormPanel.setOpened(false);
            personFormPanel.setEnabled(true);
            personFormPanel.setOpened(true);
        });
        carDetailsOk.setWidth("300px");

        carVerticalLayout.add(carDetailsOk);
        carVerticalLayout.setAlignItems(FlexComponent.Alignment.CENTER);

        TextField firstName = new TextField("Vārds*");
        firstName.setWidth("300px");
        firstName.setRequiredIndicatorVisible(true);
        firstName.setErrorMessage("Nepieciešams lauks");
        personBinder.forField(firstName).bind("firstName");

        TextField lastName = new TextField("Uzvārds*");
        lastName.setWidth("300px");
        lastName.setRequiredIndicatorVisible(true);
        lastName.setErrorMessage("Nepieciešams lauks");
        personBinder.forField(lastName).bind("lastName");

        List<String> phones = getPhoneCodes();
        Select<String> country = new Select<>();
        country.setWidth("300px");
        country.setLabel("Valsts");
        country.setItems(phones);
        personBinder.forField(country).bind("country");
        country.setValue("LV +371");

        TextField phone = new TextField("Telefona Nr*");
        phone.setWidth("300px");
        phone.setRequiredIndicatorVisible(true);
        phone.setErrorMessage("Nepieciešams lauks");
        personBinder.forField(phone).bind("phone");

        EmailField email = new EmailField();
        email.setWidth("300px");
        email.setLabel("Epasts");
        email.getElement().setAttribute("name", "email");
        email.setErrorMessage("Enter a valid email address");
        personBinder.forField(email).bind("email");

        TextField company = new TextField("Uzņēmums");
        company.setWidth("300px");
        personBinder.forField(company).bind("company");

        Button personDetailsOk = new Button("Ok", (e) -> {
            if (!validPersonInputs()) {
                return;
            }
            saveInputPerson();
            personFormPanel.setOpened(false);
            personFormPanel.setEnabled(false);
            addFinish();
        });
        personDetailsOk.setWidth("300px");

        personVerticalLayout.add(firstName, lastName, country, phone, email, company, personDetailsOk);
        personVerticalLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        personHorizontalLayout.add(personVerticalLayout, image2);

        add(accordion);

    }

    private boolean validCarInputs() {
        if (carBinder.getBean() != null) {
            Car validatingCar = carBinder.getBean();
            if (validatingCar.getManufacturer() == null || validatingCar.getManufacturer().trim().equals("")) {
                Notification notification = Notification.show("Lūdzu ievadiet auto izstrādātāju");
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                return false;
            }
            if (validatingCar.getModel() == null || validatingCar.getModel().trim().equals("")) {
                Notification notification = Notification.show("Lūdzu ievadiet auto modeli");
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                return false;
            }
            if (validatingCar.getRegistrationLicenseNo() == null || validatingCar.getRegistrationLicenseNo().trim().equals("")) {
                Notification notification = Notification.show("Lūdzu ievadiet reģistrācijas apliecības Nr.");
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                return false;
            }
            if (insurance.isEmpty()) {
                Notification notification = Notification.show("Lūdzu atzīmējiet apdrošināšanas polisi.");
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                return false;
            }
            return true;
        }
        return false;
    }

    private boolean validPersonInputs() {
        if (personBinder.getBean() != null) {
            Person validatingPerson = personBinder.getBean();
            if (validatingPerson.getFirstName() == null
                    || validatingPerson.getFirstName().trim().equals("")) {
                Notification notification = Notification.show("Lūdzu ievadiet vārdu");
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                return false;
            }
            if (validatingPerson.getLastName() == null
                    || validatingPerson.getLastName().trim().equals("")) {
                Notification notification = Notification.show("Lūdzu ievadied uzvārdu.");
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                return false;
            }
            if (validatingPerson.getPhone() == null
                    || validatingPerson.getPhone().trim().equals("")) {
                Notification notification = Notification.show("Lūdzu ievadiet telefona numuru");
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                return false;
            }
            if (validatingPerson.getEmail() == null
                    || validatingPerson.getEmail().trim().equals("")) {
                Notification notification = Notification.show("Lūdzu ievadiet e-pastu");
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                return false;
            }
            return true;
        }
        return false;
    }

    private void saveInputCar() {
        if (carBinder.getBean() != null) {
            Set<String> insurances = insurance.getSelectedItems();
            Car toBeSaved = carBinder.getBean();

            toBeSaved.setKasko(insurances.contains("KASKO"));
            toBeSaved.setOcta(insurances.contains("OCTA"));
            savedCar = this.insuranceService.saveCar(toBeSaved);
        }
    }

    private void saveInputPerson() {
        if (personBinder.getBean() != null) {
            savedPerson = this.insuranceService.savePerson(personBinder.getBean());
        }
    }

    private void addFinish() {
        remove(accordion);
        VerticalLayout handInLayout = new VerticalLayout();

        String message1 = "Nepieciešamības gadījumā Mūsu speciālists ar Jums sazināsies, lai pārrunātu Jums aktuālo jautājumu!";
        String message2 = "Speciālisti strādā pie Jūsu pieprasījuma, lai tuvākajā laikā iesniegtu iespējamos piedāvājumus";

        Button handIn = new Button("Iesniegt", (e) -> {
            remove(handInLayout);
            getSavedItems();
        });

        handInLayout.setPadding(true);
        handInLayout.setMargin(true);

        handInLayout.setAlignItems(FlexComponent.Alignment.CENTER);

        handInLayout.add(new Paragraph(message1));
        handInLayout.add(new Paragraph(message2));
        handInLayout.add(handIn);

        add(handInLayout);

    }

    private void getSavedItems() {
        VerticalLayout savedItemsLayout = new VerticalLayout();
        savedItemsLayout.setPadding(true);
        savedItemsLayout.setMargin(true);
        savedItemsLayout.setAlignItems(FlexComponent.Alignment.CENTER);

        String car = savedCar.toString();
        String person = savedPerson.toString();

        savedItemsLayout.add(new Paragraph("Saglabātie mašīnas dati datubāzē."));
        savedItemsLayout.add(new Paragraph(car));
        savedItemsLayout.add(new Paragraph("Saglabātie personas dati datubāzē."));
        savedItemsLayout.add(new Paragraph(person));

        add(savedItemsLayout);

    }

    public List<String> getPhoneCodes() {

        List<String> phones = new ArrayList<>();

        JSONParser parser = new JSONParser();
        try {

            JSONArray a = (JSONArray) parser.parse(new FileReader("src/main/java/com/example/application/data/resources/CountryCodes.json"));

            for (Object o : a) {
                JSONObject country = (JSONObject) o;

                String name = (String) country.get("code");

                String phone = (String) country.get("dial_code");

                String addit = name + " " + phone;

                phones.add(addit);
            }
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }
        return phones;
    }

    private List<Integer> getCarYearValues() {
        List<Integer> yearsList = new ArrayList<>();
        int currentYear = LocalDate.now().getYear();
        for (int i = 1970; i <= currentYear; i++) {
            yearsList.add(i);
        }
        Collections.sort(yearsList);
        Collections.reverse(yearsList);
        return yearsList;
    }


}
