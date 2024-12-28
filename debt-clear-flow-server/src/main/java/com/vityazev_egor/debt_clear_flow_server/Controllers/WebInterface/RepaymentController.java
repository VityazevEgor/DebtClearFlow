package com.vityazev_egor.debt_clear_flow_server.Controllers.WebInterface;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.vityazev_egor.debt_clear_flow_server.Misc.Shared;
import com.vityazev_egor.debt_clear_flow_server.Models.DebtRepayment;
import com.vityazev_egor.debt_clear_flow_server.Models.DebtRepaymentRepo;
import com.vityazev_egor.debt_clear_flow_server.Models.QStudent;
import com.vityazev_egor.debt_clear_flow_server.Models.QStudentRepo;
import com.vityazev_egor.debt_clear_flow_server.Models.TeacherRepo;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.io.*;


@Controller
@RequestMapping(path = "/panel/")
public class RepaymentController {
    @Autowired
    private DebtRepaymentRepo debtRepaymentRepo;

    @Autowired
    private TeacherRepo teacherRepo;

    @Autowired
    private QStudentRepo qStudentRepo;

    private final Logger logger = org.slf4j.LoggerFactory.getLogger(RepaymentController.class);

    private ModelAndView createRepayment(){
        var mv = new ModelAndView("createRepayment");
        mv.addObject("repayment", new DebtRepayment());
        mv.addObject("teachers", teacherRepo.findAll());
        return mv;
    }

    @GetMapping("createRepayment")
    public ModelAndView getCreateRepayment(){
        return createRepayment();
    }

    // создание новой отработки
    @RequestMapping(path = "createRepayment", method=RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ModelAndView postCreateRepayment(
        @RequestParam(value = "teachersLogins", required = false) String[] teacherLoginsArray,
        @Valid @ModelAttribute DebtRepayment repayment,
        BindingResult bindingResult,
        HttpSession session)
    {
        // Получаем учителей по их логинам
        if (teacherLoginsArray != null && teacherLoginsArray.length > 0) {
            repayment.setTeachersLogins(Arrays.asList(teacherLoginsArray));
        }
        logger.info("Got model in 'postCreateRepayment': "+ repayment.toString());
        if (bindingResult.hasErrors()){
            logger.error("Data didn't pass validation in 'createRepayment'");
            var errors = bindingResult.getAllErrors();
            for (var error : errors){
                logger.error(error.getDefaultMessage() + ":" + error.getObjectName());
            }
            // TODO add error message to view
            return createRepayment();
        }

        String currentTeacherLogin = session.getAttribute("login").toString();
        if (!repayment.getTeachersLogins().stream().anyMatch(login -> login.equals(currentTeacherLogin))){
            repayment.getTeachersLogins().add(currentTeacherLogin);
        }
        debtRepaymentRepo.save(repayment);
        return createRepayment();
    }

    // просмотр всех отработок созданных препеодаватлем или к которым он имеет доступ
    @GetMapping("myRepayments")
    public ModelAndView getmyRepayments(HttpSession session){
        var teacher = session.getAttribute("login").toString();
        List<DebtRepayment> debtRepayments = debtRepaymentRepo.findByTeachersLoginsContaining(teacher);
        return new ModelAndView("myRepayments", "debtRepayments", debtRepayments);
    }

    @RequestMapping(value = "/delete/{rpid}", method = RequestMethod.GET)
    public ModelAndView deleteRepayment(@PathVariable("rpid") Integer id){
        logger.info("Got model in 'deleteRepayment': "+ id.toString());
        debtRepaymentRepo.deleteById(id);
        logger.info("Deleted model!");
        return new ModelAndView("redirect:/panel/myRepayments");
    }

    private ModelAndView getViewRepaymentModelById(Integer id){
        var repayment = debtRepaymentRepo.findById(id).get();
        List<QStudent> qStudents  = qStudentRepo.findByDebtRepaymentOrderByIdAsc(repayment);
        var mv  = new ModelAndView("viewRepayment");
        mv.addObject("repayment", repayment);
        mv.addObject("qStudents", qStudents);
        return mv;
    }

    @RequestMapping(value = "/view/{rpid}", method = RequestMethod.GET)
    public ModelAndView viewRepayment(@PathVariable("rpid") Integer id){
        logger.info("Got model in 'viewRepayment': "+ id.toString());
        return getViewRepaymentModelById(id);
    }


    // в этом методе буду использовать https://commons.apache.org/proper/commons-csv/user-guide.html
    @RequestMapping(value  =  "/view/{rpid}", method  = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ModelAndView addStudents(@RequestPart MultipartFile csvFile, @RequestPart String descriptionColumnName, @PathVariable("rpid") Integer id){
        var repayment = debtRepaymentRepo.findById(id).get();
        if (csvFile.getSize() > 0){
            logger.info("Got file! File size = " + csvFile.getSize());
            String fileName = java.util.UUID.randomUUID().toString() + ".csv";
            File file = Paths.get(Shared.csvFilesDirectory.toString(), fileName).toFile();
            try {
                csvFile.transferTo(file);
                logger.info("Saved csv file as "+fileName);

                FileReader in = new FileReader(file, StandardCharsets.UTF_8);
                Iterable<CSVRecord> records = CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).setTrim(true).build().parse(in);
                for (CSVRecord record : records) {
                    List<String> columnsData = record.toList();
                    var qStudent  = new QStudent();
                    qStudent.setDebtRepayment(repayment);

                    for (String data : columnsData){
                        if (Shared.isEmail(data)){
                            qStudent.setEmail(data);
                        }
                        if (Shared.isFullName(data)){
                            qStudent.setFullName(data);
                        }
                    }
                    
                    if (!descriptionColumnName.isBlank()){
                        qStudent.setWorkDeskription(record.get(descriptionColumnName));
                    }

                    if (qStudent.checkEmailAndFullName()){
                        qStudentRepo.save(qStudent);
                        logger.info("Saved qStudent!");
                        System.out.print(qStudent.toString());
                    }

                }
            } catch (IllegalStateException e) {
                logger.error("Can't tranfer csv file", e);
            } catch (IOException e) {
                logger.error("Can't write csv file", e);
            }
        }
        else{
            logger.error("Can't get file");
        }
        return getViewRepaymentModelById(id);
    }

    // очистить список записанных студентов
    @RequestMapping(value  =  "/clearAll/{rpid}", method  = RequestMethod.GET)
    public ModelAndView clearAll(@PathVariable("rpid") Integer id){
        var repayment = debtRepaymentRepo.findById(id).get();
        List<QStudent> setudents  = qStudentRepo.findByDebtRepaymentOrderByIdAsc(repayment);
        qStudentRepo.deleteAll(setudents);
        return new ModelAndView("redirect:/panel/view/"+id.toString());
    }

}
