<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="org.springframework.http.HttpStatus"%>
<%@ page import="com.doritech.api.Entity.*"%>
<%@ page import="com.doritech.api.DTO.EmployeeMasterDTO"%>
<%@ page import="com.doritech.api.Entity.ParamEntity"%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Email Scheduler</title>

    <link rel="stylesheet" href="./css/deviceLogDetails.css" />
    <link rel="stylesheet" href="./css/multilingual.css" />
    <link href="./fonts/poppins.css" rel="stylesheet">
    <link rel="stylesheet" href="./externalCSS/bootstrap.min.css" />
    <script src="./externalJS/fontawesome.js"></script>

    <!-- DATATABLES CSS Links -->
    <link rel="stylesheet" href="./externalCSS/dataTables.bootstrap4.min.css">
    <link rel="stylesheet" href="./externalCSS/buttons.dataTables.min.css">
    <link rel="stylesheet" href="./externalCSS/responsive.bootstrap4.min.css">

    <!-- DATATABLES JS Links -->
    <script src="./externalJS/sweetalert2.all.min.js"></script>
    <script src="./externalJS/jquery-3.7.0.js"></script>
    <script src="./externalJS/jquery.dataTables.min.js"></script>
    <script src="./externalJS/dataTables.bootstrap4.min.js"></script>
    <script src="./externalJS/responsive.bootstrap4.min.js"></script>
    <script src="./externalJS/dataTables.responsive.min.js"></script>
    <script src="./externalJS/dataTables.buttons.min.js"></script>
    <script src="./externalJS/pdfmake.min.js"></script>
    <script src="./externalJS/vfs_fonts.js"></script>
    <script src="./externalJS/buttons.html5.min.js"></script>
    <script src="./externalJS/buttons.print.min.js"></script>
    <style>
        .checkbox-inline input[type="checkbox"] {
            margin-top: 10px;
        }
    </style>
</head>
<body style="font-size: 12px; overflow-y: hidden; font-family:Poppins;">
    <div id="branchCode" style="display: none;" >${branchCode}</div>

    <div class="row mx-1">
        <div class="col-md-12">
            <div class="row">
                <div class="col-md-12">
                    <h5 class="heading">Email Scheduler</h5>
                </div>
            </div>
        </div>
    </div>
    <div class="row">
        <div class="col-md-12">
            <div class="m-2">
                <div class="card">
                    <table id="example" class="table table-striped table-bordered">
                        <thead>
                            <tr>
                                <th>Branch Name</th>
                                <th>Branch Code</th>
                                <th>Time Period</th>
                                <th>Action</th>
                            </tr>
                        </thead>
                        <tbody id="inputListTableBody">
                            <c:forEach var="branch" items="${branchData.payload}">
                                <c:forEach var="childBranch" items="${branch.children}">
                                    <c:forEach var="subChildBranch" items="${childBranch.children}">
                                        <tr>
                                            <td>${subChildBranch.branchDesc}</td>
                                            <td>${subChildBranch.branchCode}</td>
                                            <td style="text-align: center">
                                                <label class="checkbox-inline">
                                                    <input type="checkbox" name="timePeriod" value="Daily" 
                                                        <c:forEach var="record" items="${allRecords}">
                                                            <c:if test="${record.branchId eq subChildBranch.branchCode && record.timePeriod eq 'Daily'}">checked</c:if>
                                                        </c:forEach>> Daily
                                                </label>
                                                <label class="checkbox-inline">
                                                    <input type="checkbox" name="timePeriod" value="Weekly" 
                                                        <c:forEach var="record" items="${allRecords}">
                                                            <c:if test="${record.branchId eq subChildBranch.branchCode && record.timePeriod eq 'Weekly'}">checked</c:if>
                                                        </c:forEach>> Weekly
                                                </label>
                                                <label class="checkbox-inline">
                                                    <input type="checkbox" name="timePeriod" value="Monthly" 
                                                        <c:forEach var="record" items="${allRecords}">
                                                            <c:if test="${record.branchId eq subChildBranch.branchCode && record.timePeriod eq 'Monthly'}">checked</c:if>
                                                        </c:forEach>> Monthly
                                                </label>
                                            </td>
                                            <td style="text-align: center">
                                                <button class="btn btn-sm  btn-primary save-btn">Save</button>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                    <tr>
                                        <td>${childBranch.branchDesc}</td>
                                        <td>${childBranch.branchCode}</td>
                                        <td style="text-align: center">
                                            <label class="checkbox-inline">
                                                <input type="checkbox" name="timePeriod" value="Daily" 
                                                    <c:forEach var="record" items="${allRecords}">
                                                        <c:if test="${record.branchId eq childBranch.branchCode && record.timePeriod eq 'Daily'}">checked</c:if>
                                                    </c:forEach>> Daily
                                            </label>
                                            <label class="checkbox-inline">
                                                <input type="checkbox" name="timePeriod" value="Weekly" 
                                                    <c:forEach var="record" items="${allRecords}">
                                                        <c:if test="${record.branchId eq childBranch.branchCode && record.timePeriod eq 'Weekly'}">checked</c:if>
                                                    </c:forEach>> Weekly
                                            </label>
                                            <label class="checkbox-inline">
                                                <input type="checkbox" name="timePeriod" value="Monthly" 
                                                    <c:forEach var="record" items="${allRecords}">
                                                        <c:if test="${record.branchId eq childBranch.branchCode && record.timePeriod eq 'Monthly'}">checked</c:if>
                                                    </c:forEach>> Monthly
                                            </label>
                                        </td>
                                        <td style="text-align: center">
                                            <button class="btn btn-sm btn-primary save-btn">Save</button>
                                        </td>
                                    </tr>
                                </c:forEach>
                                <tr>
                                    <td>${branch.branchDesc}</td>
                                    <td>${branch.branchCode}</td>
                                    <td style="text-align: center">
                                        <label class="checkbox-inline">
                                            <input type="checkbox" name="timePeriod" value="Daily" 
                                                <c:forEach var="record" items="${allRecords}">
                                                    <c:if test="${record.branchId eq branch.branchCode && record.timePeriod eq 'Daily'}">checked</c:if>
                                                </c:forEach>> Daily
                                        </label>
                                        <label class="checkbox-inline">
                                            <input type="checkbox" name="timePeriod" value="Weekly" 
                                                <c:forEach var="record" items="${allRecords}">
                                                    <c:if test="${record.branchId eq branch.branchCode && record.timePeriod eq 'Weekly'}">checked</c:if>
                                                </c:forEach>> Weekly
                                        </label>
                                        <label class="checkbox-inline">
                                            <input type="checkbox" name="timePeriod" value="Monthly" 
                                                <c:forEach var="record" items="${allRecords}">
                                                    <c:if test="${record.branchId eq branch.branchCode && record.timePeriod eq 'Monthly'}">checked</c:if>
                                                </c:forEach>> Monthly
                                        </label>
                                    </td>
                                    <td style="text-align: center">
                                        <button class="btn btn-sm btn-primary save-btn">Save</button>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
<script>

$(document).ready(function() {
    $('.save-btn').on('click', function() {
        const row = $(this).closest('tr');
        const branchDesc = row.find('td').eq(0).text();
        const branchCode = row.find('td').eq(1).text();
        const timePeriods = row.find('input[name="timePeriod"]:checked').map(function() {
            return $(this).val();
        }).get();
        var timePeriodArray=[];
        var timePeriodArrayForSave=[];
        timePeriods.forEach(function(timePeriod) {
        	 const data = {
                     branchId: branchCode,
                     branchName: branchDesc,
                     timePeriod: timePeriod
                 };

        	 
        	 
            $.ajax({
                url: '/getEmailSchedulerByBranchIdAndTimePeriod',
                type: 'GET',
                data: {
                    branchId: branchCode,
                    timePeriod: timePeriod
                },
                success: function(response) {
                	
                	timePeriodArray.push(timePeriod);
                	if(timePeriodArray.length>1){
                		Swal.fire({
                            icon: 'warning',
                            title: 'Email Scheduler Already Exists',
                            text: 'Email Scheduler already exists for ' + timePeriodArray,
                            confirmButtonText: 'OK'
                        });
                	}else if(timePeriodArray.length>2){
                		Swal.fire({
                            icon: 'warning',
                            title: 'Email Scheduler Already Exists',
                            text: 'Email Scheduler already exists for ' + timePeriodArray,
                            confirmButtonText: 'OK'
                        });
                	}else{
                		Swal.fire({
                            icon: 'warning',
                            title: 'Email Scheduler Already Exists',
                            text: 'Email Scheduler already exists for ' + timePeriodArray,
                            confirmButtonText: 'OK'
                        });
                	}
                	
                	//alert(timePeriodArray);
                	 
                   // alert('Email Scheduler already exists for ' + timePeriod);
                },
                error: function(error) {
                    console.log('Error checking Email Scheduler existence:', error);

                   
                    
                    $.ajax({
                        url: '/saveEmailSchedularService',
                        type: 'POST',
                        contentType: 'application/json',
                        data: JSON.stringify(data),
                        success: function(response) {
                        	timePeriodArrayForSave.push(data.timePeriod)
                        	 console.log('Success:', response);
                             Swal.fire({
                                 icon: 'success',
                                 title: 'Saved!',
                                 text: 'Email Scheduler details saved successfully for ' + timePeriodArrayForSave,
                                 confirmButtonText: 'OK'
                             }).then(() => {
                                 window.location.reload();
                             });
                         },
                        error: function(error) {
                            console.error('Error saving Email Scheduler details:', error);
                            alert('An error occurred while saving Email Scheduler details for ' + timePeriod);
                        }
                    });
                }
            });
        });
    });
});

    </script>



	<script src="./js/navBarToggle.js"></script>
	<script src="./externalJS/bootstrap.bundle.min.js"></script>
	
	<script src="./js/tmsDataTables.js"></script>
</body>
</html>