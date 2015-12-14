/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

include('../db.jag');
include('../constants.jag')
var helper = require('as-data-util.js');

function buildInfoBoxGreaterThan1200DaysSql(selectStatement, type, whereClause) {
    return 'SELECT ' + selectStatement + '(' + type + ') as value, YEAR(time) as time ' +
           'FROM REQUESTS_SUMMARY_PER_MINUTE ' + whereClause + ' GROUP BY YEAR(time);';
}

function buildInfoBoxGreaterThan90Days(selectStatement, type, whereClause) {
    return 'SELECT ' + selectStatement + '(' + type + ') as value, ' +
           'DATE_FORMAT(time, \'%b %Y\') as time ' +
           'FROM REQUESTS_SUMMARY_PER_MINUTE ' + whereClause + ' GROUP BY MONTH(time);';
}

function buildInfoBoxGreaterThan30Days(selectStatement, type, whereClause) {
    return 'SELECT ' + selectStatement + '(' + type + ') as value, ' +
           'CONCAT(DATE_FORMAT(DATE_ADD(time, INTERVAL (1 - DAYOFWEEK(time)) DAY),\'%b %d %Y\'), \' - \', ' +
           'DATE_FORMAT(DATE_ADD(time, INTERVAL (7 - DAYOFWEEK(time)) DAY),\'%b %d %Y\')) as time ' +
           'FROM REQUESTS_SUMMARY_PER_MINUTE ' + whereClause + ' GROUP BY WEEK(time);';
}

function buildInfoBoxGreaterThan1Day(selectStatement, type, whereClause) {
    return 'SELECT ' + selectStatement + '(' + type + ') as value, ' +
           'DATE_FORMAT(time, \'%b %d %Y\') as time ' +
           'FROM REQUESTS_SUMMARY_PER_MINUTE ' + whereClause + ' GROUP BY DATE(time);';
}

function buildInfoBoxLessThan1Day(selectStatement, type, whereClause) {
    return 'SELECT ' + selectStatement + '(' + type + ') as value, ' +
           'DATE_FORMAT(time, \'%H:00\') as time ' +
           'FROM REQUESTS_SUMMARY_PER_MINUTE ' + whereClause + ' GROUP BY HOUR(time);';
}

function getDataForInfoBoxBarChart(type, conditions) {
    var startTime = helper.parseDate(request.getParameter('start_time'));
    var endTime = helper.parseDate(request.getParameter('end_time'));
    var timeDiff = 0;
    var i, len;
    var sql;
    var results;
    var arrList = [];

    if (request.getParameter('start_time') != null && request.getParameter('end_time') != null) {
        timeDiff = Math.abs((endTime.getTime() - startTime.getTime()) / 86400000);
    } else {
        timeDiff = 1;
    }

    var selectStatement = 'SUM';
    if (type == 'averageResponseTime') {
        selectStatement = 'AVG';
    }

    if (timeDiff > 1200) {
        sql = buildInfoBoxGreaterThan1200DaysSql(selectStatement, type, conditions.sql);
    } else if (timeDiff > 90) {
        sql = buildInfoBoxGreaterThan90Days(selectStatement, type, conditions.sql);
    } else if (timeDiff > 30) {
        sql = buildInfoBoxGreaterThan30Days(selectStatement, type, conditions.sql);
    } else if (timeDiff > 1) {
        sql = buildInfoBoxGreaterThan1Day(selectStatement, type, conditions.sql);
    } else if (timeDiff <= 1) {
        sql = buildInfoBoxLessThan1Day(selectStatement, type, conditions.sql);
    }

    results = executeQuery(sql, conditions.params);

    for (i = 0, len = results.length; i < len; i++) {
        var tempData = [];
        tempData[0] = i;
        tempData[1] = results[i]['value'];
        tempData[2] = results[i]['time'] + ' : ' + results[i]['value'];
        arrList.push(tempData);
    }
    return arrList;
}

function getInfoBoxRequestStat(conditions) {
    var output = {};

    var results = getAggregateDataFromDAS(REQUEST_SUMMARY_TABLE, conditions, "0", ALL_FACET, [
        {
            "fieldName": AVERAGE_REQUEST_COUNT,
            "aggregate": "SUM",
            "alias": "SUM_" + AVERAGE_REQUEST_COUNT
        }, {
            "fieldName": AVERAGE_REQUEST_COUNT,
            "aggregate": "MIN",
            "alias": "MIN_" + AVERAGE_REQUEST_COUNT
        }, {
            "fieldName": AVERAGE_REQUEST_COUNT,
            "aggregate": "MAX",
            "alias": "MAX_" + AVERAGE_REQUEST_COUNT
        }, {
            "fieldName": AVERAGE_REQUEST_COUNT,
            "aggregate": "AVG",
            "alias": "AVG_" + AVERAGE_REQUEST_COUNT
        }
    ]);

    results = JSON.parse(results)[0];

    output['title'] = 'Total Requests';
    output['measure_label'] = 'Per min';

    if (results != null && results['values']['SUM_' + AVERAGE_REQUEST_COUNT] != null) {
        results = results['values'];
        output['total'] = results['SUM_' + AVERAGE_REQUEST_COUNT];
        output['max'] = results['MAX_' + AVERAGE_REQUEST_COUNT];
        output['avg'] = Math.round(results['AVG_' + AVERAGE_REQUEST_COUNT]);
        output['min'] = results['MIN_' + AVERAGE_REQUEST_COUNT]
    } else {
        output['total'] = output['max'] = output['avg'] = output['min'] = 'N/A';
    }
    
    // todo: enable mini-chart for average requests
    //output['graph'] = getDataForInfoBoxBarChart('averageRequestCount', conditions);
    
    print(output);
}

function getInfoBoxResponseStat(conditions) {
    var output = {};

    var results = getAggregateDataFromDAS(REQUEST_SUMMARY_TABLE, conditions, "0", ALL_FACET, [
        {
            "fieldName": AVERAGE_RESPONSE_TIME,
            "aggregate": "MIN",
            "alias": "MIN_" + AVERAGE_RESPONSE_TIME
        }, {
            "fieldName": AVERAGE_RESPONSE_TIME,
            "aggregate": "MAX",
            "alias": "MAX_" + AVERAGE_RESPONSE_TIME
        }, {
            "fieldName": AVERAGE_RESPONSE_TIME,
            "aggregate": "AVG",
            "alias": "AVG_" + AVERAGE_RESPONSE_TIME
        }
    ]);

    results = JSON.parse(results)[0];

    output['title'] = 'Response Time';
    output['measure_label'] = 'ms';

    if (results != null && results['values']['MAX_' + AVERAGE_RESPONSE_TIME] != null) {
        results = results['values'];
        output['max'] = results['MAX_' + AVERAGE_RESPONSE_TIME];
        output['avg'] = Math.round(results['AVG_' + AVERAGE_RESPONSE_TIME]);
        output['min'] = results['MIN_' + AVERAGE_RESPONSE_TIME];
    } else {
        output['max'] = output['avg'] = output['min'] = 'N/A';
    }
    // todo: enable mini-chart for average response time
    //output['graph'] = getDataForInfoBoxBarChart('averageResponseTime', conditions);

    print(output);
}

function getInfoBoxSessionStat(conditions) {
    var output = {};

    var results = getAggregateDataFromDAS(REQUEST_SUMMARY_TABLE, conditions, "0", ALL_FACET, [
        {
            "fieldName": SESSION_COUNT,
            "aggregate": "SUM",
            "alias": "SUM_" + SESSION_COUNT
        }, {
            "fieldName": SESSION_COUNT,
            "aggregate": "AVG",
            "alias": "AVG_" + SESSION_COUNT
        }
    ]);

    results = JSON.parse(results)[0];

    output['title'] = 'Session';

    if (results != null && results['values']['SUM_' + SESSION_COUNT] != null) {
        results = results['values'];
        output['total'] = results['SUM_' + SESSION_COUNT];
        output['avg'] = Math.round(results['AVG_' + SESSION_COUNT]);
    } else {
        output['total'] = output['avg'] = 'N/A';
    }

    print(output);
}

function getInfoBoxErrorStat(conditions) {
    var output = {};

    var results = getAggregateDataFromDAS(REQUEST_SUMMARY_TABLE, conditions, "0", ALL_FACET, [
        {
            "fieldName": HTTP_SUCCESS_COUNT,
            "aggregate": "SUM",
            "alias": "SUM_" + HTTP_SUCCESS_COUNT
        }, {
            "fieldName": HTTP_ERROR_COUNT,
            "aggregate": "SUM",
            "alias": "SUM_" + HTTP_ERROR_COUNT
        }
    ]);

    results = JSON.parse(results)[0];

    output['title'] = 'Errors';

    if (results != null && results['values']['SUM_' + HTTP_ERROR_COUNT] != null) {
        results = results['values'];
        output['total'] = results['SUM_' + HTTP_ERROR_COUNT];
        output['percentage'] = 
                (results['SUM_' + HTTP_ERROR_COUNT] * 100 / results['SUM_' + HTTP_SUCCESS_COUNT]).toFixed(2) + '\x25';
    } else {
        output['total'] = output['percentage'] = 'N/A';
    }

    print(output);
}