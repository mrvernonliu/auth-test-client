import React, { Component } from "react";
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
        fetch("http://localhost:8080/api/testdata", {
            method: "GET",
            headers: {"Accept": "application/json"}
        }).then((response) => {
            this.setState({
                responseCode: response["status"]
            });
            return response.text();
        }).then((responseJson) => {
            this.setState({
                message: responseJson
            });
            console.log(this.state);
        });
    }

    displayData() {
        let message = this.state.message;
        let textColor = "green";
        if (this.state.responseCode != 200) {
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