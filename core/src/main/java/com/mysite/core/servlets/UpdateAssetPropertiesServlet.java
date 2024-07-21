package com.mysite.core.servlets;


import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.propertytypes.ServiceDescription;
import org.osgi.service.component.propertytypes.ServiceVendor;

import javax.servlet.Servlet;
import java.io.FileInputStream;
import java.io.InputStream;

@Component(
        service = { Servlet.class },
        property = {
                "sling.servlet.paths=/bin/updateAssetPropertiesFromDAM",
                "sling.servlet.methods=POST"
        }
)
@ServiceDescription("Servlet to update asset properties from Excel in DAM")
@ServiceVendor("Example")
public class UpdateAssetPropertiesServlet extends
        SlingAllMethodsServlet {

    @Override
    protected void doPost(SlingHttpServletRequest request,
                          SlingHttpServletResponse response) {
        try {
            ResourceResolver resourceResolver = request.getResourceResolver();
            String excelFilePath = request.getParameter("excelFilePath");

            Resource excelResource =
                    resourceResolver.getResource(excelFilePath);
            if (excelResource == null) {
                response.getWriter().write("Excel file not found in DAM.");
                return;
            }

            FileInputStream fileInputStream = excelResource.adaptTo(FileInputStream.class);
            HSSFWorkbook workbook = new HSSFWorkbook(fileInputStream);
            HSSFSheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                Cell pathCell = row.getCell(0);
                Cell propertyNameCell = row.getCell(1);
                Cell propertyValueCell = row.getCell(2);

                if (pathCell != null && propertyNameCell != null &&
                        propertyValueCell != null) {
                    String assetPath = pathCell.getStringCellValue();
                    String propertyName = propertyNameCell.getStringCellValue();
                    String propertyValue =
                            propertyValueCell.getStringCellValue();

                    Resource assetResource =
                            resourceResolver.getResource(assetPath);
                    if (assetResource != null) {
                        ModifiableValueMap properties =
                                assetResource.adaptTo(ModifiableValueMap.class);
                        if (properties != null) {
                            properties.put(propertyName, propertyValue);
                            resourceResolver.commit();
                        }
                    }
                }
            }

            workbook.close();
            response.getWriter().write("Asset properties updated successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
