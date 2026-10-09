<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="ISO-8859-1"%>
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
<title>DI-HMS</title>


<link rel="stylesheet" href="./css/employeeMaster.css" />
<link rel="stylesheet" href="./externalCSS/bootstrap.min.css" />
<link href="./fonts/poppins.css" rel="stylesheet">
<script src="./externalJS/fontawesome.js"></script>

<!-- DATATABLES CSS Links -->

<link rel="stylesheet"
	href="./externalCSS/dataTables.bootstrap4.min.css">
<link rel="stylesheet" href="./externalCSS/buttons.dataTables.min.css">
<link rel="stylesheet"
	href="./externalCSS/responsive.bootstrap4.min.css">

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


<script src="https://cdn.jsdelivr.net/npm/sweetalert2@10"></script>
<script src="https://cdn.jsdelivr.net/npm/axios/dist/axios.min.js"></script>

<style type="text/css">
td a {
	color: black;
	text-decoration: none !important;
}
</style>

</head>
<body style="font-size: 12px; font-family:Poppins;">
<div class="container-fluid">
    <div class="row ">
        <div class="col-12">
            <div class="row">
                <div class="col-12 col-md-8">
                    <h5 class="heading">User Master</h5>
                </div>
                <div class="col-12 col-md-4  mt-md-0 d-flex align-items-center mb-2 ">
                    		<form id="uploadForm" enctype="multipart/form-data">
        <input class="form-control me-2" type="file" name="file" id="formFile">
        <label for="formFile" class="btn btn-sm btn-primary" style="background-color:#028bd7;">Upload File</label>
    </form>
                    <div class="d-flex mx-4">
                        <button type="button" class="btn btn-sm importButton me-2 mb-1 mr-4" data-toggle="tooltip" data-placement="top" title="Upload File">
                            <i class="fa-solid fa-upload" onClick="importEmployeeMaster();"></i>
                        </button>
                        <button type="button" class="btn btn-sm importButton mb-1" data-toggle="tooltip" data-placement="top" title="Download File">
                            <i class="fa-solid fa-download" onClick="exportEmployeeMaster();"></i>
                        </button>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<div class="row">
    <div class="col-md-12 col-sm-12">
        <div class="card-container mx-2 ">
            <div class="row">
                <div class="col-md-4">
                    <div class="card" style="">
                        <div class="">
                            <table id="example" class="table table-striped table-bordered">
                                <thead>
                                    <tr>
                                        <th>Code</th>
                                        <th>Name</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach items="${employeeMaster.payload}" var="employeeMaster">
                                        <tr>
                                            <td><a href="#" onclick="fetchEmployee('${employeeMaster.empId}')">${employeeMaster.empId}</a></td>
                                            <td><a href="#" onclick="fetchEmployee('${employeeMaster.empId}')">${employeeMaster.empName}</a></td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>

                <div class="col-md-8">
                    <div class="card h-100">
                        <div class="ml-2 mr-2">
                            <div class="row">
                                <div class="col-md-12">
                                    <div class="mt-2"></div>
                                    <form id="empForm" name="empForm">
                                        <div style="border: 1.75px solid #028bd7;">
                                            <b style="margin-left: 0.5%; color: #003d87;">User Details</b>
                                            <div class="mb-3 row" style="margin: 1%;">
                                                <label class="custom-field one col-md-4">
                                                    <input type="text" placeholder="" class="form-control form-control-sm" id="empId" value="${employeeMasterDTO.empId}" readonly />
                                                    <span class="placeholder">User ID</span>
                                                </label>
                                                <label class="custom-field one col-md-4">
                                                    <input type="text" placeholder="" class="form-control form-control-sm " id="empName" />
                                                    <span class="placeholder">Name</span>
                                                </label>
                                               <!--  <label class="custom-field one col-md-4 " style="display:none">
                                                    <input type="text" placeholder="" class="form-control form-control-sm" maxlength="10" id="empPan" />
                                                    <span class="placeholder">
                                                        
                                                        <div id="panError" style="color: red; display: none; font-weight: bold; font-size: 14px; display: inline;"></div>
                                                        PAN
                                                        
                                                    </span>
                                                    
                                                </label> -->
                                                
                                                <div class="custom-field one col-md-4">
                                                    <div class="input-boxx active-greyy">
                                                        <label class="input-labell">Select Role</label>
                                                        <select class="input-11 form-control form-control-sm " name="empRole" id="empRole">
                                                            <option value="Role">--Select Role--</option>
                                                            <option value="admin">Data Admin</option>
                                                            <option value="super admin">Super User</option>
                                                            <option value="user">CRO/Security guard</option>
                                                            <option value="security officer">Security Officer</option>
                                                        </select>
                                                    </div>
                                                </div>
                                            </div>
                                            <div class="mb-3 row" style="margin: 1%;">
                                                
                                                <div class="custom-field one col-md-4">
                                                    <div class="input-boxx active-greyy">
                                                        <label class="input-labell">Select Group</label>
                                                        <select class="input-11 form-control form-control-sm" id="groupId" name="groupId">
                                                            <option value="1">--Select Group--</option>
                                                            <c:forEach var="group" items="${groupList}">
                                                                <option value="${group.groupId}">${group.groupId}->${group.groupName}</option>
                                                            </c:forEach>
                                                        </select>
                                                    </div>
                                                </div>
                                                <div class="custom-field one col-md-4">
                                                    <div class="input-boxx active-greyy">
                                                        <label class="input-labell">Select Branch</label>
                                                        <select class=" input-11 form-control form-control-sm " name="branch" id="branch">
                                                            <option value="#">--Select Branch--</option>
                                                            <c:forEach var="branches" items="${branchList}">
                                                                <option value="${branches.branchCode}">${branches.branchDesc}</option>
                                                            </c:forEach>
                                                        </select>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                        <div class="mt-2"></div>
                                        <div style="border: 1.75px solid #028bd7;">
                                            <b style="margin-left: 0.5%; color: #003d87;">Address Details</b>
                                            <div class="mb-2 row" style="margin: 0.5%;">
                                                <label class="custom-field one col-md-12">
                                                    <input type="text" placeholder="" class="form-control form-control-sm " id="address" />
                                                    <span class="placeholder">Address</span>
                                                </label>
                                            </div>
                                            
                                            <div class="mb-2 row" style="margin: 0.5%;">
                                                <label class="custom-field one col-md-2">
                                                    <input type="text" placeholder="" class="form-control form-control-sm " id="pin" />
                                                    <span class="placeholder">Pincode</span>
                                                </label>
                                                <label class="custom-field one col-md-3">
                                                    <input type="text" placeholder="" class="form-control form-control-sm"  id="city" />
                                                    <span class="placeholder">City</span>
                                                </label>
                                                <label class="custom-field one col-md-3">
                                                    <input type="text" placeholder="" class="form-control form-control-sm " id="state" />
                                                    <span class="placeholder">State</span>
                                                </label>
                                                <label class="custom-field one col-md-4">
                                                    <input type="text" placeholder="" class="form-control form-control-sm " id="country" />
                                                    <span class="placeholder">Country</span>
                                                </label>
                                            </div>
                                            
                                        </div>
                                        <div class="mt-2"></div>
                                        <div style="border: 1.75px solid #028bd7;">
                                            <b style="margin-left: 0.5%; color: #003d87;">Contact Details</b>
                                            <div class="mb-2 row" style="margin: 0.5%;">
                                            
                                                <label class="custom-field one col-md-4">
                                                    <input type="number" placeholder="" class="form-control form-control-sm " id="mobile" maxlength="13" />
                                                    <span class="placeholder">
                                                        <div id="mobileError" style="color: red; display: none; font-size: 14px; font-weight: bold; display: inline;"></div>Mobile
                                                        
                                                    </span>
                                                    
                                                </label>
                                                
                                                <label class="custom-field one col-md-6">
                                                    <input type="email" placeholder="" class="form-control form-control-sm" id="email" />
                                                    <span class="placeholder">
                                                        <div id="emailError" style="color: red; display: none; font-size: 14px; font-weight: bold; display: inline;"></div>  E-mail
                                                        
                                                    </span>
                                                     
                                                </label>
                                                
                                            </div>
                                        </div>
                                        
                                        <div class="row">
                                            <div class="col-md-12 mt-4">
                                                <div class="form-btn">
                                                    <button type="button" class="btn btn-primary" id="editButton" style="display:none;" onclick="editEmployee();">Confirm</button>
                                                    <button type="button" class="btn btn-primary" id="saveEmployeeButton" onclick="saveNewEmployee();">Save</button>
                                                    <button type="button" class="btn btn-danger"  id="resetButton" onclick="resetForm();">Reset</button>
                                                </div>
                                            </div>
                                        </div>
                                    </form>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>



<script>
document.addEventListener("DOMContentLoaded", function () {
    let userRole = sessionStorage.getItem("userRole");

    if (userRole === "super admin") {
        document.getElementById("saveEmployeeButton").style.display = "none";
        document.getElementById("editButton").style.display = "none";
        document.getElementById("resetButton").style.display = "none";
        
        
    }
});


</script>


	<script src="./js/navBarToggle.js"></script>
	<script src="./externalJS/bootstrap.bundle.min.js"></script>
	<script src="./js/employeeMaster.js"></script>
	<script src="./js/tmsDataTables.js"></script>
	
</body>
</html>