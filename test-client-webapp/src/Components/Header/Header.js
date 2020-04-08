import React, { Component } from "react";
import "./Header.css";

export default class Header extends Component {

    constructor() {
        super();
        this.state = {
            loggedIn: false,
            applicationTitle: "placeholder",
            loginUrl: ""
        }
    }

    componentWillMount() {
        let host = process.env.REACT_APP_LOCALDEV_API_URL || "";
        fetch(host + "/api/testdata/navbar", {
            method: "GET",
            headers: {"Accept": "application/json"}
        }).then((response) => {
            return response.json()
        }).then((response) => {
            console.log(response);
            this.setState({
                applicationTitle: response["APPLICATION_TITLE"].replace(/"/g,""),
                loginUrl: response["LOGIN_URL"].replace("http://", "").replace("https://", "").replace(/"/g,"")
            })
            console.log(this.state);
        })
    }

    loggedIn() {
        if (this.state.loggedIn) {
            return (
                <li className="nav-item active">
                    <div className="nav-link">User <span className="sr-only">(current)</span></div>
                </li>   
            )
        }

        return (
            <li className="nav-item active">
                <a className="nav-link login" href={"//" + this.state.loginUrl}>Login <span className="sr-only">(current)</span></a>
            </li>
        )
    }

    getServiceName() {
        let title = process.env.REACT_APP_TITLE || "PLACEHOLDER"
        return <div className="navbar-brand">{this.state.applicationTitle}</div>
    }

    render() {
        return (
            <nav className="navbar navbar-expand-lg navbar-light bg-light">
                {this.getServiceName()}
                <button className="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarText" aria-controls="navbarText" aria-expanded="false" aria-label="Toggle navigation">
                    <span className="navbar-toggler-icon"></span>
                </button>
                <div className="collapse navbar-collapse justify-content-end" id="navbarText">
                    <ul className="navbar-nav">
                        {this.loggedIn()}
                    </ul>
                </div>
            </nav>
        )
    }
}
    