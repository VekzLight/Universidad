<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql"%>

<html>
<head>
    <meta   http-equiv="content-type"
            content="text/html";
            charset="windows-1252" />
          
    <meta   name="GENERATOR"
            content="SeaMonkey/2.40 [en] (Windows; 10; Intel(R) Core(TM) i7-4500U CPU @ 1.80GHz 2.40 GHz) [Composer]" />
    
    <meta   name="Author" 
            content="DNA System" />
    
    <title>Forma de Búsqueda de Cliente</title>
    
    <link   rel="stylesheet"
            href="${pageContext.request.contextPath}/css/style.css" />
</head>
<body>
    <form   method="get"
            action="${ pageContext.request.contextPath }/cliente_search.jsp">
        
        <div class="step">Forma de Búsqueda de Cliente</div>
        <div class="instructions">Proporciona la información de búsqueda solicitada</div>
        
        <br>
        
        <c:set var="pattern" value="${param.pattern}" />
        <c:if test="${param.pattern == null || pattern == ''}">
            <c:set var="pattern" value="%" />
        </c:if>
        <%--
            String pattern = request.getParameter("pattern");
            if(pattern == null || pattern.equal(""))
                pattern = "%";
            pageContext.setAttribute("pattern", pattern);
        --%>
        
        <table border="1" cellpadding="10">
            <tr>
                <td align="center">
                    <table>
                        <tr class="form">
                            <td align="right">
                                <div class="label">Patrón:</div>
                            </td>
                            <td><input name="pattern" size="10" value="${pattern}"></td>
                            <td><input type="submit" value="  Buscar  "></td>
                        </tr>
                    </table>
                </td>
            </tr>
        </table>
        
        <br>
        
        <table border="0" width="100%">
            <tr class="form">
                <td align="center">
                    <div class="label">Nombre</div>
                </td>
                <td align="center">
                    <div class="label">Paterno</div>
                </td>
                <td align="center">
                    <div class="label">Materno</div>
                </td>
                <td align="center">
                    <div class="label">Detalle</div>
                </td>
                <td align="center">
                    <div class="label">PDF</div>
                </td>
                <td align="center">
                    <div class="label">XML</div>
                </td>
            </tr>
            
            <sql:query var="resultados" dataSource="jdbc/TestDS">
                SELECT * FROM cliente WHERE apellido_paterno_cliente LIKE ? ORDER BY 3;
                <sql:param value="${pattern}"/>
            </sql:query>
            <c:forEach var="fila" items="${ resultados.rows }">
               <tr>
                    <td align="center">${ fila.nombre_cliente }</td>
                    <td align="center">${ fila.apellido_paterno_cliente }</td>
                    <td align="center">${ fila.apellido_materno_cliente }</td>
                    <td align="center">
                        <input type="button"
                            value="  Ver  "
                            onclick="window.location='cliente_view.jsp'" />
                    </td>
                    <td align="center"><input type="button"
                        value="  Ver como PDF  "></td>
                    <td align="center"><input type="button"
                        value="  Ver como XML  "></td>
                </tr>
            </c:forEach>
        </table>
        
        <br>
        
        <input type="button" value="  Regresar  "
            onclick="window.location='${ pageContext.request.contextPath }/main.jsp'">
    </form>
    <br>
</body>
</html>