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


<link rel="stylesheet" href="./css/branchMaster.css" />
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

<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/sweetalert2@10"></script>
<script src="https://cdn.jsdelivr.net/npm/axios/dist/axios.min.js"></script>
<!-- Select2 CSS -->
<link href="https://cdn.jsdelivr.net/npm/select2@4.1.0-rc.0/dist/css/select2.min.css" rel="stylesheet" />
<!-- jQuery (required by Select2) -->
<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<!-- Select2 JS -->
<script src="https://cdn.jsdelivr.net/npm/select2@4.1.0-rc.0/dist/js/select2.min.js"></script>

<style type="text/css">
td a {
	color: black;
	text-decoration: none !important;
}


 #formFile {
            display: none;
        }
</style>

</head>
<body style="font-size: 12px; overflow-y: visible; font-family:Poppins;">
<input id="branchCode" value="${loggedInUserBranchId}" style="display: none;">
<div class="container-fluid">
    <div class="row mx-1">
        <div class="col-12">
            <div class="row">
                <div class="col-12 col-md-6 mt-2">
                    <h5 class="heading">Branch Master</h5>
                </div>
                <div class="col-12 col-md-6 mt-2 mt-md-0 mb-2 d-flex align-items-center">
                    <input class="form-control me-2 col-md-5" type="text" id="searchInput" placeholder="Search by Branch Code" />
                    <div id="suggestionDropdown" class="mt-1"></div>
                    <button type="button" class="btn btn-sm importButton me-2 mx-1" id="searchButton" onclick="searchByBranchCode()">
                        <i class="fa-solid fa-search" style="color: #fff;"></i>
                    </button>
                      		<form id="uploadForm" enctype="multipart/form-data">
        <input class="form-control me-2" type="file" name="file" id="formFile">
        <label for="formFile" class="btn btn-sm btn-primary mt-2" style="background-color:#028bd7;">Upload File</label>
    </form>
                    <button type="button" class="btn btn-sm importButton  ml-1" data-toggle="tooltip" data-placement="top" title="Upload File" onClick="importBranchMaster();">
                        <i class="fa-solid fa-upload"></i>
                    </button>
                    <button type="button" class="btn btn-sm importButton" data-toggle="tooltip" data-placement="top" title="Download File" style="margin-left:6px;">
                            <i class="fa-solid fa-download" onClick="exportBranchMaster();"></i>
                        </button>
                </div>
            </div>
        </div>
    </div>
</div>

<div class="row">
    <div class="col-md-12 col-sm-12">
        <div class="card-container d-flex">
          <div class="col-md-4">
             
                <div class="card " style=" min-height: 450px; max-height:450px; overflow-y:auto;">
                <div><h2 style="text-align: center;">Branch Hierarchy</h2></div>
                    <div class="" style=" min-height: 400px; max-height:400px; overflow-y:auto;">
                        <div id="branchTree" class="vertical-tree"></div>
                    </div>
                </div>
            </div>
            <div class="col-md-8 col-sm-12">
                <div class="card h-80" style=" min-height: 450px; max-height:450px; overflow-y:auto; overflow-x:hidden;">
                    <div class="ml-2 mr-2">
                        <div class="row" style=" min-height: 410px; max-height:410px; overflow-y:auto; overflow-x:hidden;">
                            <div class="col-md-12">
                                <div class="mt-2"></div>
                                <form id="itemIssueForm" name="itemIssueForm">
                                    <div style="border: 1.75px solid #028bd7;" class="mb-1">
                                        <b style="margin-left: 0.5%; color: #003d87;">Branch Details</b>
                                        <div class="mb-2 row" style="margin: 1%;">
                                            <label class="custom-field one col-md-3" style="display: none;">
                                                <input type="text" placeholder="" class="form-control form-control-sm" id="branchCode" />
                                                <span class="placeholder">Branch Code</span>
                                            </label>
                                            <label class="custom-field one col-md-5">
                                                <input type="text" placeholder="" class="form-control form-control-sm " id="branchDesc" />
                                                <span class="placeholder">Branch Name</span>
                                            </label>
                                            <label class="custom-field one col-md-4">
                                                <input type="text" placeholder="" class="form-control form-control-sm" id="bank" value="" />
                                                <span class="placeholder">Bank Name </span>
                                            </label>
                                            <div class="custom-field one col-md-3 mt-1">
                                                <div class="input-boxx active-greyy">
                                                    <label class="input-labell">Branch Type</label>
                                                    <select class="input-11 form-control" id="branchType" name="deviceType">
                                                        <option id="deviceType" value="#">--select--</option>
                                                        <c:forEach var="branch" items="${branchType}">
                                                            <option id="deviceType" value="${branch.descp1}">${branch.descp1}</option>
                                                        </c:forEach>
                                                    </select>
                                                </div>
                                            </div>
             
<div class="custom-field one col-md-3 ">
                                                <div class="input-boxx active-greyy">
                                                    <label class="input-labell">Parent Branch</label>
                                                    <select class="input-11 form-control" id="parentCode" name="parentCode" disabled>
                                                        <option value="0">--select--</option>
                                                    </select>
                                                </div>
                                            </div>
                           <label class="custom-field one col-md-3 mt-1">
    <select class="form-control form-control-sm" id="ifsc" name="ifsc" style="width: 100%;">
        <option value="">Select Branch</option>
    </select>

    <!-- ?? Hidden input to store FGMO code for Zone API -->
    <input type="hidden" id="fgmoCodeForZone" name="fgmoCodeForZone" />
</label>

                                             <label class="custom-field one col-md-3 mt-1">
                                                <input type="text" placeholder="" class="form-control form-control-sm" id="latitude" name="latitude" value="" />
                                                <span class="placeholder">Latitude</span>
                                            </label>
                                            <label class="custom-field one col-md-3 mt-1">
                                                <input type="text" placeholder="" class="form-control form-control-sm" id="longitude" name="longitude" value="" />
                                                <span class="placeholder">Longitude</span>
                                            </label>
                                           
                                        </div>
                                    </div>
                                    <div style="border: 1.75px solid #028bd7;" class="mb-1" >
                                        <div style="background:white;z-index:1;position:sticky; top:1px;"> <b style="margin-left: 0.5%; color: #003d87; "> Contact Details</b></div>
                                        <div class="mb-2 row" style="margin: 1%;">

                                           
											
											<label class="custom-field one col-md-3">
                                                <input type="number" placeholder="" class="form-control form-control-sm" id="telephoneNo" maxlength="13" />
                                                <span class="placeholder">
                                                    <div id="telephoneerror" style="color: red; display: none; font-size: 14px; font-weight: bold; display: inline;"></div>
                                                    Telephone Number</span>
                                            </label>
 <label class="custom-field one col-md-3">
                                                <input type="number" placeholder="" class="form-control form-control-sm " id="faxNo" />
                                                <span class="placeholder">Fax No</span>
                                            </label>
                                            <label class="custom-field one col-md-3">
                                                <input type="number" placeholder="" class="form-control form-control-sm" id="mobile" maxlength="13" />
                                                <span class="placeholder">
                                                    <div id="mobileError" style="color: red; display: none; font-size: 14px; font-weight: bold; display: inline;"></div>
                                                    Mobile</span>
                                            </label>
                                            
                                             <label class="custom-field one col-md-3">
												<input type="number" placeholder="" class="form-control form-control-sm" id="mob2" maxlength="13" />
												<span class="placeholder">
                                                    <div id="phoneError2" style="color: red; display: none; font-size: 14px; font-weight: bold; display: inline;"></div>
                                                    Mobile-2
                                                    
                                                </span>
											</label>

                                           
                                        </div>
                                        <div class="mb-2 row" style="margin: 1%;">
                                            
                                            <label class="custom-field one col-md-3">
												<input type="number" placeholder="" class="form-control form-control-sm" id="mob3" maxlength="13" />
												<span class="placeholder">
                                                    <div id="phoneError3" style="color: red; display: none; font-size: 14px; font-weight: bold; display: inline;"></div>
                                                    Mobile-3
                                                    
                                                </span>
												
											</label>
                                            <label class="custom-field one col-md-3">
												<input type="number" placeholder="" class="form-control form-control-sm" id="mob4" maxlength="13" />
												<span class="placeholder">
                                                    <div id="phoneError4" style="color: red; display: none; font-size: 14px; font-weight: bold; display: inline;"></div>
                                                    Mobile-4
                                                    
                                                </span>
												
											</label>
											 <label class="custom-field one col-md-3">
                                                
												<input type="number" placeholder="" class="form-control form-control-sm" id="mob5" maxlength="13" />
												<span class="placeholder">
                                                    
                                                    <div id="phoneError5" style="color: red; display: none; font-size: 14px; font-weight: bold; display: inline;"></div>
                                                    Mobile-5
                                                
                                                </span>
												
											</label>
											
											
                                            <label class="custom-field one col-md-3">
												<input type="number" placeholder="" class="form-control form-control-sm" id="mob6" maxlength="13" />
												<span class="placeholder">
                                                    <div id="phoneError6" style="color: red; display: none; font-size: 14px; font-weight: bold; display: inline;"></div>
                                                    Mobile-6
                                                    
                                                </span>
												
											</label>
                                        </div>
                                        <div class="mb-2 row" style="margin: 1%;">
                                            
                                           
											  <label class="custom-field one col-md-3">
                                                <input type="email" placeholder="" class="form-control form-control-sm" id="email" />
                                                <span class="placeholder">
                                                    <div id="emailError" style="color: red; display: none; font-size: 14px; font-weight: bold; display: inline;"></div>
                                                    Email
                                                    
                                                </span>
                                                
                                            </label>    
                                            
                                            <label class="custom-field one col-md-3">
                                                <input type="email" placeholder="" class="form-control form-control-sm" id="email2" />
                                                <span class="placeholder">
                                                    <div id="emailError2" style="color: red; display: none; font-size: 14px; font-weight: bold; display: inline;"></div>
                                                    Email-2
                                                </span>
                                                  
                                            </label>
                                                    <label class="custom-field one col-md-3">
                                                <input type="email" placeholder="" class="form-control form-control-sm" id="email3" />
                                                <span class="placeholder">
                                                    <div id="emailError3" style="color: red; display: none; font-size: 14px; font-weight: bold; display: inline;"></div>
                                                    Email-3
                                                </span>
                                                   
                                            </label>
                                            <label class="custom-field one col-md-3">
                                                <input type="email" placeholder="" class="form-control form-control-sm" id="email4" />
                                                <span class="placeholder">
                                                    <div id="emailError4" style="color: red; display: none; font-size: 14px; font-weight: bold; display: inline;"></div>
                                                    Email-4                                                    
                                                </span>
                                                   
                                            </label>         
                                        </div>
										
                                        <div class="mb-2 row" style="margin: 1%;">
                                          
                                            <label class="custom-field one col-md-3">
                                                <input type="email" placeholder="" class="form-control form-control-sm" id="email5" />
                                                <span class="placeholder">
                                                    <div id="emailError5" style="color: red; display: none; font-size: 14px; font-weight: bold; display: inline;"></div>
                                                    Email-5                                                   
                                                </span>
                                                   
                                            </label>
                                            <label class="custom-field one col-md-3">
                                                <input type="email" placeholder="" class="form-control form-control-sm" id="email6" />
                                                <span class="placeholder">
                                                    <div id="emailError6" style="color: red; display: none; font-size: 14px; font-weight: bold; display: inline;"></div>
                                                    Email-6                             
                                                </span>
                                                   
                                            </label>
                                        </div>
                                    </div>
                                    <div style="border: 1.75px solid #028bd7;" class="mb-1">
                                        <b style="margin-left: 0.5%; color: #003d87;"> Address Details</b>
										
                                        <div class="mb-2 row" style="margin: 1%;">
                                            <label class="custom-field one col-md-12">
                                                <input type="text" placeholder="" class="form-control form-control-sm" id="address1" value="" />
                                                <span class="placeholder">Address</span>
                                            </label>
                                        </div>
										
                                        <div class="mb-2 row" style="margin: 1%;">
                                            <label class="custom-field one col-md-4">
                                                <input type="text" placeholder="" class="form-control form-control-sm " id="address3" />
                                                <span class="placeholder">Pincode</span>
                                            </label>
                                            <label class="custom-field one col-md-4">
                                                <input type="text" placeholder="" class="form-control form-control-sm"  id="address2" />
                                                <span class="placeholder">City</span>
                                            </label>
                                            <label class="custom-field one col-md-4">
                                                <input type="text" placeholder="" class="form-control form-control-sm " id="address4" />
                                                <span class="placeholder">State</span>
                                            </label>
                                        </div>
                                    </div>
                                    <div style="border: 1.75px solid #028bd7;" class="mb-1">
                                        <b style="margin-left: 0.5%; color: #003d87;">Other Details</b>
                                        <div class="mb-2 row" style="margin: 1%;">

                                         
                                             <label class="custom-field one col-md-3">
                                                <input type="text" placeholder="" class="form-control form-control-sm" id="contractPerson" name="contractPerson" value="" />
												<span class="placeholder">Contact Person</span>
											</label>                                      

                                            <div class="custom-field one col-md-4">
                                                <div class="input-boxx active-greyy">
                                                    <label class="input-labell">Status</label>
                                                    <select class="input-11 form-control" id="status" name="deviceType">
                                                        <option id="deviceType" value="#">--select--</option>
                                                        <option id="deviceType" value="Active">Active</option>
                                                        <option id="deviceType" value="InActive">InActive</option>
                                                    </select>
                                                </div>
                                            </div>
                                            
                                        </div>
                                    </div>
                                  
                                </form>
                            </div>
                        </div>
                        <div class="col-md-12 mt-1" style="position:sticky;">
                                        <div class="form-btn">
                                            <button type="button" class="btn btn-primary" id="saveButton" onclick="saveBranch();">Save</button>
                                        </div>
                                        <div class="form-btn">
                                            <button type="button" class="btn btn-info" id="editButton" style="display:none;" onclick="editBranch();">Confirm</button>
                                        </div>
                                         <div class="form-btn">
                                            <button  type="button" class="btn btn-info btn-danger" id="deleteButton" style="display:none;" onclick="deleteBranch();"><i class="fa-regular fa-trash-can fa-lg"></i></button>
                                        </div>
                                    </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>



	<script src="./js/navBarToggle.js"></script>
	<script src="./externalJS/bootstrap.bundle.min.js"></script>
	<script src="./js/branchMaster.js"></script>
	<script src="./js/tmsDataTables.js"></script>
	<script src="./externalJS/multilingual.js"></script>
	<script src="./js/googleTranslateElementInit.js"></script>


</body>
</html>