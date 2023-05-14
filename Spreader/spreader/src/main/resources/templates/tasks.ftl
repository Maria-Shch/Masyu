<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Tasks Info</title>
    <link rel="stylesheet" href="https://cdn.datatables.net/1.13.3/css/jquery.dataTables.min.css">
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.2.0/css/bootstrap.min.css">
</head>
<body>
<div id="header" style="align-content: center">
    <h2>Masyu Puzzle Task Info</h2>
</div>
<div id="content">
    <table id="tasksTable" class="display" style="width:100%">
        <thead>
        <tr>
            <th>Task Details</th>
            <th>Task UUID</th>
            <th>Status</th>
            <th>Computational Node</th>
            <th>Result</th>
            <th>Started When</th>
            <th>Completed When</th>
        </tr>
        </thead>
        <tbody>
        <#if model["taskList"]??>
            <#list model["taskList"] as task>
                <tr>
                    <td>${task.details}</td>
                    <td>${task.uuid}</td>
                    <td>${task.status}</td>
                    <#if task.node??>
                        <td>${task.node}</td>
                    <#else>
                        <td></td>
                    </#if>
                    <#if task.result??>
                        <td>${task.result}</td>
                    <#else>
                        <td></td>
                    </#if>
                    <#if task.startedWhen??>
                        <td>${task.startedWhen}</td>
                    <#else>
                        <td></td>
                    </#if>
                    <#if task.completedWhen??>
                        <td>${task.completedWhen}</td>
                    <#else>
                        <td></td>
                    </#if>
                </tr>
            </#list>
        </#if>
        </tbody>
        <tfoot>
        <tr>
            <th>Task Details</th>
            <th>Task UUID</th>
            <th>Status</th>
            <th>Computational Node</th>
            <th>Result</th>
            <th>Started When</th>
            <th>Completed When</th>
        </tr>
        </tfoot>
    </table>
</div>
<script src="https://code.jquery.com/jquery-3.5.1.js"></script>
<script src="https://cdn.datatables.net/1.13.3/js/jquery.dataTables.js"></script>
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.2.0/js/bootstrap.min.js"></script>
<script>
    $(document).ready(function () {
        $('#tasksTable').DataTable(
            {
                order: [[6, 'desc']]
            }
        );
    });
</script>
</body>
</html>