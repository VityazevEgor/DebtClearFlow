package com.vityazev_egor.debt_clear_flow_server.Controllers.WebInterface;

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

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.*;
import java.io.*;


@Controller
@RequestMapping(path = "/panel/")
public class RepaymentController {
    @Autowired
    private DebtRepaymentRepo debtRepaymentRepo;

    @Autowired
    private QStudentRepo qStudentRepo;

    private final Logger logger = org.slf4j.LoggerFactory.getLogger(RepaymentController.class);

    private ModelAndView createRepayment(){
        var mv = new ModelAndView("createRepayment");
        mv.addObject("repayment", new DebtRepayment());
        return mv;
    }

    @GetMapping("createRepayment")
    public ModelAndView getCreateRepayment(){
        return createRepayment();
    }

    // создание новой отработки
    @RequestMapping(path = "createRepayment", method=RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ModelAndView postCreateRepayment(@Valid @ModelAttribute DebtRepayment repayment, BindingResult bindingResult, HttpSession session){
        logger.info("Got model in 'postCreateRepayment': "+ repayment.toString());
        if (bindingResult.hasErrors()){
            logger.error("Data didn't pass validation in 'createRepayment'");
            var errors = bindingResult.getAllErrors();
            for (var error : errors){
                logger.error(error.getDefaultMessage() + ":" + error.getObjectName());
            }
        }
        else{
            logger.info("Data passed validation in 'createRepayment'");
            // добовляем логин преподователя, который создал эту отработку
            repayment.setTeachersLogins(repayment.getTeachersLogins()+", " + session.getAttribute("login").toString());
            debtRepaymentRepo.save(repayment);
            logger.info("Saved model!");
        }
        return createRepayment();
    }

    // просмотр всех отработок созданных препеодаватлем или к которым он имеет доступ
    @GetMapping("myRepayments")
    public ModelAndView getmyRepayments(HttpSession session){
        List<DebtRepayment> debtRepayments = debtRepaymentRepo.findByTeachersLoginsContaining(session.getAttribute("login").toString());
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
        List<QStudent> qStudents  = qStudentRepo.findByDebtRepaymentIdOrderByIdAsc(repayment.getId());
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
    public ModelAndView addStudents(@RequestPart MultipartFile csvFile, @PathVariable("rpid") Integer Id){
        if (csvFile.getSize() > 0){
            logger.info("Got file! File size = " + csvFile.getSize());
            String fileName = java.util.UUID.randomUUID().toString() + ".csv";
            File file = Paths.get(Shared.csvFilesDirectory.toString(), fileName).toFile();
            try {
                csvFile.transferTo(file);
                logger.info("Saved csv file as "+fileName);

                FileReader in = new FileReader(file);

                //                 8

                // Since Apache Commons CSV v1.9.0, the withSkipHeaderRecord() & the withFirstRecordAsHeader() methods are deprecated. A builder interface is provided. Use it thusly:

                // CSVFormat.DEFAULT.builder()
                //     .setHeader()
                //     .setSkipHeaderRecord(true)
                //     .build();
                Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().withTrim().parse(in);
                for (CSVRecord record : records) {
                    logger.info("Got record:  "  + record.get(1) + ":"+record.get(1));
                    //record.toList()
                    // TODO сделать автоматическое определение того где находиться почта, а где имя и фамелия
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
        return getViewRepaymentModelById(Id);
    }

}
