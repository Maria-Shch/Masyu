<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Nodes Info</title>
    <link rel="stylesheet" href="https://cdn.datatables.net/1.13.3/css/jquery.dataTables.min.css">
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.2.0/css/bootstrap.min.css">
</head>
<body>
<div id="header" style="align-content: center">
    <h2>Registered Computational Nodes</h2>
</div>
<div id="content">
    <table id="nodesTable" class="display" style="width:100%">
        <thead>
        <tr>
            <th>Remote Host</th>
            <th>Remote Port</th>
            <th>Status</th>
            <th>Registration Time</th>
            <th>Last Health Check Time</th>
            <th>Current Task UUID</th>
            <th>Current Task Details</th>
        </tr>
        </thead>
        <tbody>
        <#if model["nodeList"]??>
            <#list model["nodeList"] as node>
                <tr>
                    <td>${node.remoteHost}</td>
                    <td>${node.remotePort}</td>
                    <td>Active</td>
                    <td>${node.registrationTime}</td>
                    <#if node.lastHealthCheckTime??>
                        <td>${node.lastHealthCheckTime}</td>
                    <#else>
                        <td></td>
                    </#if>
                    <#if node.currentTaskUuid??>
                        <td>${node.currentTaskUuid}</td>
                    <#else>
                        <td></td>
                    </#if>
                    <#if node.currentTaskDetails??>
                        <td>${node.currentTaskDetails}</td>
                    <#else>
                        <td></td>
                    </#if>
                </tr>
            </#list>
        </#if>
        </tbody>
        <tfoot>
        <tr>
            <th>Remote Host</th>
            <th>Remote Port</th>
            <th>Status</th>
            <th>Registration Time</th>
            <th>Last Health Check Time</th>
            <th>Current Task UUID</th>
            <th>Current Task Details</th>
        </tr>
        </tfoot>
    </table>
</div>
<script src="https://code.jquery.com/jquery-3.5.1.js"></script>
<script src="https://cdn.datatables.net/1.13.3/js/jquery.dataTables.js"></script>
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.2.0/js/bootstrap.min.js"></script>
<script>
    $(document).ready(function () {
        $('#nodesTable').DataTable();
        $('.config-modal-body').each(function () {
            if ($(this).val().length !== 0) {
                $(this).html(JSON.stringify($(this).html(), undefined, 2));
            }
        });
    });
</script>
</body>
</html>