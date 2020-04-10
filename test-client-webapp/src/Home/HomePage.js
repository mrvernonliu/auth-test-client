import React, { Component } from "react";
import Cookie from "js-cookie";
import "./HomePage.css";

export default class HomePage extends Component {

    constructor(props) {
        super(props);
        this.state = {
            message: "loading",
            responseCode: "200"
        }
    }

    componentDidMount() {
        let host = process.env.REACT_APP_LOCALDEV_API_URL || "";
        fetch(host + "/api/testdata", {
            method: "GET",
            headers: {"Accept": "application/json"},
            credentials: "include"
        }).then((response) => {
            this.setState({
                responseCode: response["status"]
            });
            if (response["status"] !== 200) this.logout();
            return response.text();
        }).then((responseJson) => {
            this.setState({
                message: responseJson
            });
        });
    }

    logout() {
        let host = process.env.REACT_APP_LOCALDEV_API_URL || "";
        fetch(host + "/api/sso/invalidate", {
            method: "POST",
            headers: {"Accept": "application/json"}
        }).then((response) => {
            return response.json();
        }).then((response) => {
            let domain = response["DOMAIN"];
            let path = response["PATH"];
            Cookie.remove('ti', { path: path, domain: domain })
            Cookie.remove('ta', { path: path, domain: domain })
        })
    }

    displayData() {
        let message = this.state.message;
        let textColor = "green";
        if (this.state.responseCode !== 200) {
            textColor = "red";
        }
        return <p className = {textColor}>{message}</p>
    }

    render() {
        return (
            <div style={{marginTop: "2em"}}>{this.displayData()}</div>
        );
    }
}