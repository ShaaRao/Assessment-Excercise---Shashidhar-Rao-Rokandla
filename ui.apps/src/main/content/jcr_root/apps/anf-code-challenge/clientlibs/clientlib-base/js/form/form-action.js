$(document).on("click", "#userForm form button", function () {
    if (!$('[name="firstName"]').val() || !$('[name="lastName"]').val() || !$('[name="age"]').val()) {
        alert('Form is incomplete, please input missing fields!');
    } else {
        $.getJSON('/etc/age.json', {}, function (data) {
            let minAge = data.minAge;
            let maxAge = data.maxAge;
            var reqParams = {
                'firstName': $('[name="firstName"]').val(),
                'lastName': $('[name="lastName"]').val(),
                'age': $('[name="age"]').val()
            }
            if (Number(minAge) <= Number(reqParams.age) && Number(maxAge) >= Number(reqParams.age)) {

                $.get('/bin/saveUserDetails', reqParams, function (data) {
                    if (data === "OK") {
                        alert('User data inserted');
                    } else {
                        alert('Something went wrong!');
                    }
                });
            } else {
                alert("Age should be between" + Number(data.minAge) + "&" + Number(data.maxAge));
            }
        });
    }
    return false;
});