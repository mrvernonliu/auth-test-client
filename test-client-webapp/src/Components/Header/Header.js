import React, { Component } from "react";
import "./Header.css";

export default class Header extends Component {

    constructor() {
        super();
        this.state = {
            loggedIn: false
        }
    }

    loggedIn() {
        if (this.state.loggedIn) {
            return (
                <li className="nav-item active">
                    <a className="nav-link" href="#">User <span className="sr-only">(current)</span></a>
                </li>   
            )
        }
        return (
            <li className="nav-item active">
                <a className="nav-link" href="http://auth.vernonliu.com/login?clientUuid=5d94fdf7-85d0-49c0-a5c2-1b87424bd716&redirectUrl=https://vernonliu.com">Login <span className="sr-only">(current)</span></a>
            </li>
        )
    }

    getServiceName() {
        let title = process.env.REACT_APP_TITLE || "PLACEHOLDER"
        return <div className="navbar-brand">{title}</div>
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
    