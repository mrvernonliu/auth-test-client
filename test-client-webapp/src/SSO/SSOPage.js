import React, { Component } from "react";
import * as qs from 'query-string';

export default class SSOPage extends Component {

    constructor(props) {
        super(props);
        this.state = {
            accessCode: this.getAccessCode() || "error"
        }
        console.log(this.state.accessCode);
        //TODO: POST to API with accessCode and have the API return jwt cookies
    }

    getAccessCode() {
        var accessCode = qs.parse(this.props.location.search)["accessCode"];
        if (accessCode) return accessCode;
        return null;
    }

    componentWillMount() {
        fetch("/api/login/sso?accessCode=" + this.state.accessCode, {
            method: "POST",
        }).then((response) => {
            response.text();
        }).then((response) => {
            console.log(response);
        })
    }

    render() {
        // If the render function is called, something went wrong
        return (
            <p>Something went wrong</p>
        );
    }
}