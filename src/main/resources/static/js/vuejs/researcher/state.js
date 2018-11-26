let state = {
    user: {
        name: "",
        roles: []
    },
    searchData: {
        "acronyms": [],
        "designs": [],
        "diseases": [],
        "sex": [],
        "ethnicity": [],
        "specTypes": []
    },
    selectedValues: {
        "acronyms": [],
        "designs": [],
        "diseases": [],
        "sex": [],
        "ethnicity": [],
        "specTypes": [],
        "columns": []
    },
    errorMessage: "",
    columns: [
        {
            label: 'Acronym',
            field: 'acronym',
            numeric: false,
            html: false
        },
        {
            label: 'Nb-spec',
            field: 'nbSamples',
            numeric: true,
            html:false
        }
    ],
    dataColumns: [
        {
            label: 'Acronym',
            field: 'acronym',
            numeric: false,
            html: false
        },
        {
            label: 'EGA Access',
            field: 'egaAccess',
            numeric: false,
            html: false
        }
    ],
    row: {},
    specimenRows: [],
    datasetRows: [],
    selectedColumns: [],
    selectedRows: [],
    selectedDatasets: [],
    nbRowSelected: 0,
    projectId: null,
    selectedCart: null
};

let cartColumns = [
    {
        label: 'Acronym',
            field: 'acronym',
        numeric: false,
        html: false
    },
    /*{
        label: 'Design',
        field: 'design',
        numeric: false,
        html: false
    },
    {
        label: 'Disease',
        field: 'disease',
        numeric: false,
        html: false
    },*/
    {
        label: 'Nb Request',
        field: 'nbRequest',
        numeric: true,
        html: false
    }
];

let projectState = {
    cards: []
};

/*
 * Remove an element from an array
 */
function removeElement (arr, item) {
    if (arr.length) {
        let index = arr.indexOf(item);
        if (index > -1) {
            return arr.splice(index, 1)
        }
    }
}

/*
 * Add the checked value and its corresponding column
 */
function addSelectedValue(arr, value, label, field) {
    // arr.push(value);
    if (label !== "Acronym") {
        if (arr.length === 1) {
            state.columns.splice(
                state.columns.length - 1,
                0,
                {label: label, field: field, numeric: false, html: false}
            );
            state.selectedValues.columns.push(label)
        }
    }

}

/*
 * Remove the unchecked value and its corresponding column
 */
function removeSelectedValue(arr, value, label) {
    // removeElement(arr, value);
    if (label !== "Acronym") {
        if (arr.length === 0) {
            const item = state.columns.find(item => item.label === label);
            state.columns.splice(state.columns.indexOf(item), 1);
            state.selectedValues.columns.splice(state.selectedValues.columns.indexOf(label), 1);
        }
    }
}

/*
 * Reconstruct row in an easy row format
 */
function adjustRow(row) {
    let tempRow = {};
    _.each(row, function (val, key) {
        switch (key) {
            case 'acronym':
                tempRow['acronym'] = val;
                break;
            case 'design':
                tempRow['design'] = val;
                break;
            case 'disease':
                tempRow['disease'] = val;
                break;
            case 'sex':
                tempRow['sex'] = val;
                break;
            case 'ethnicity':
                tempRow['ethnicity'] = val;
                break;
            case 'country':
                tempRow['country'] = val;
                break;
            case 'specType':
                tempRow['specType'] = val;
                break;
        }
    });
    return tempRow;
}

/*
 * The columns to show in the query column modal
 */
function otherColumns(row) {
    // TODO: ADD THE OTHER COLUMNS
    let tempRow = {};
    _.each(row, function (val, key) {
        switch (key) {
            case 'sex':
                tempRow['sex'] = val;
                break;
            case 'ethnicity':
                tempRow['ethnicity'] = val;
                break;
        }
    });
    return tempRow;
}

/*
 * Add selected biospecimen to array (selectedRows)
 */
function addIfNotExistsObjectArray(arr, row) {
    let selectedRow = adjustRow(row);
    const item = arr.find(item => {
        let tempRow = adjustRow(item);
        return _.isEqual(tempRow, selectedRow)
    });
    if (_.isUndefined(item) && !_.isUndefined(selectedRow)) {
        state.nbRowSelected += 1;
        selectedRow['nbRequest'] = row.nbRequest;
        selectedRow['query'] = otherColumns(row);
        arr.push(selectedRow);
    } else {
        item.nbRequest = row.nbRequest;
    }
}

/*
 * Remove biospecimen from array (selectedRows)
 */
function removeObjectArray(arr, row) {
    const item = arr.find(item => _.isEqual(item, row));
    if (!_.isUndefined(item)) {
        arr.splice(arr.indexOf(item), 1);
        state.nbRowSelected -= 1;
    }
}

/*
 * Add acronym and EGA access row to array 
 */
function addDatasetToCart(row) {
    const item = state.selectedDatasets.find(item => _.isEqual(item, row));
    if (_.isUndefined(item)) {
        state.selectedDatasets.push(row);
        state.nbRowSelected += 1;
    }
}