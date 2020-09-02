let ws = null;
const url = 'http://ec2-54-145-147-192.compute-1.amazonaws.com:8080/java-websocket-0.0.1-SNAPSHOT/rest/';
// const url = 'http://localhost:8080/prattle/rest/'
let loginAttempts = 0;
let messageRecipientUsername = "";
const channels = [];
let signedInUsername = null;
let moderator = false;
let systemModerator = false;

/**************************************************************************************************
 * Creation of Account
 **************************************************************************************************/

/**
 * This function is triggered when the "Submit and Create Account" button is pressed.
 * It captures the values in the form, and sends it to the function that creates an account.
 */
function createAccountButton() {
    const username = document.getElementById("usernameNew").value.toLowerCase();
    console.log(username);
    const password = document.getElementById("passwordNew").value;
    const firstName = document.getElementById("first").value;
    const lastName = document.getElementById("last").value;
    const email = document.getElementById("email").value;
    const bio = document.getElementById("bio").value;
    const failed = document.getElementById("failedCreation");
    let successCreation;

    if (emptyFields(username, password, firstName, lastName, email, bio)) {
        failed.setAttribute("class", "visible");
    } else {
        createAccount(username, password, firstName, lastName, email, bio, function (status) {
            successCreation = (status === 200);
            if (typeof successCreation !== "undefined") {
                if (!successCreation) {
                    failed.setAttribute("class", "visible");
                } else {
                    document.getElementById("signupForm").reset();
                    document.getElementById("successCreation")
                        .setAttribute("class", "visible");
                    swapSignIn();
                }
            }
        });
    }
}

/**
 * Creates an account by initializing an HTTP request, taking the 6 values the user enters in the
 * form, and sending them via a POST request to create a new user.
 * @param username the username - must be unique
 * @param password the password
 * @param firstName the first name
 * @param lastName the last name
 * @param email the email - must be unique
 * @param bio short summary
 * @param cb not sure what this is
 */
function createAccount(username, password, firstName, lastName, email, bio, cb) {
    const xhr = new XMLHttpRequest();
    xhr.onreadystatechange = function () {
        if (this.readyState === 4) {
            cb(this.status);
        }
    };
    xhr.open("POST", url + "user/create", true);
    xhr.setRequestHeader('Content-Type', 'application/json');
    const newUserJSON = JSON.stringify({
                                           username: username,
                                           password: password,
                                           email: email,
                                           firstName: firstName,
                                           lastName: lastName,
                                           bio: bio
                                       });
    xhr.send(newUserJSON);

    const x = new XMLHttpRequest();
    x.open("PUT", "rest/channel/1/user/" + username, false);
    x.send();
}

/**
 * Check to make sure fields are empty.
 */
function emptyFields(a, b, c, d, e, f) {
    return a == null
           || a === ""
           || b == null
           || b === ""
           || c == null
           || c === ""
           || d == null
           || d === ""
           || e == null
           || e === ""
           || f == null
           || f === "";
}

/**************************************************************************************************
 * Login
 **************************************************************************************************/

/**
 * Tries to login. If successful, initializes setup of chat, and websocket.
 */
function connect() {
    const username = document.getElementById("username").value.toLowerCase();
    signedInUsername = username;
    const password = document.getElementById("password").value;
    const host = document.location.host;
    const pathname = document.location.pathname;
    let success;

    login(username, password, function (status) {
        success = status;

        if (typeof success !== "undefined" && checkLogin(success) === 0) {
            appendLists(username);

            ws = new WebSocket("ws://" + host + pathname + "cm/1/" + signedInUsername);
            addActiveState(1);
            msgInit("channel", 1);

            ws.onmessage = function (event) {
                const message = JSON.parse(event.data);
                createMessage(message);
            };
        }
    });
}

/**
 * Attempts to login, keeps track of failed logins, and begins to initialize chat if appropriate.
 * @param success if the username/password combination worked
 * @returns {number} 1 for success, 0 if failure
 */
function checkLogin(success) {
    const signIn = document.getElementById("SignInContainer");
    const bowShot = document.getElementById("ParentContainer");
    const failedLogin = document.getElementById("failedLogin");
    const noUser = document.getElementById("noUser");

    if (success === 401) {
        const countDisplay = document.getElementById("errorCount");
        failedLogin.setAttribute("class", "visible");
        noUser.setAttribute("class", "hidden");
        loginAttempts++;
        countDisplay.innerHTML = (4 - loginAttempts);
        return 1;
    } else if (success === 404) {
        failedLogin.setAttribute("class", "hidden");
        noUser.setAttribute("class", "visible");
        return 1;
    } else if (success === 403) {
        failedLogin.innerHTML = "Exceeded maximum login attempts. You have"
                                + " been locked out.";
        document.getElementById("signInButton").disabled = true;
        return 1;
    } else {

        signIn.classList.remove("visible");
        signIn.classList.add("hidden");
        bowShot.classList.remove("hidden");
        bowShot.classList.add("visible");
        return 0;
    }
}

/**
 * Takes in the username and password and CB and sends a JSON string via a POST request.
 * @param username username
 * @param password password
 * @param cb
 */
function login(username, password, cb) {
    const xhr = new XMLHttpRequest();
    xhr.onreadystatechange = function () {
        if (this.readyState === 4) {
            cb(this.status);
        }
    };
    xhr.open("POST", url + 'user/login', true);
    xhr.setRequestHeader('Content-Type', 'application/json');
    const userJSON = JSON.stringify({
                                        username: username,
                                        password: password
                                    });
    xhr.send(userJSON);
}

/**
 * Switches between the 'create account' form and the 'login' form for the user.
 */
function swapSignIn() {
    const signInPage = document.getElementById("SignInInner");
    const createAccountPage = document.getElementById("CreateAccount");

    if (signInPage.classList.contains('visible')) {
        signInPage.classList.remove('visible');
        signInPage.classList.add('hidden');
        createAccountPage.classList.add('visible');
        createAccountPage.classList.remove('hidden');
    } else {
        createAccountPage.classList.remove('visible');
        createAccountPage.classList.add('hidden');
        signInPage.classList.add('visible');
        signInPage.classList.remove('hidden');
    }
}

/**************************************************************************************************
 * Creating Interface Lists
 **************************************************************************************************/

/**
 * Sends a GET request and retrieves a list
 * @param urlUserList the URL to send via XMLHttpRequest
 * @returns string of something in JSON format
 */
function getJSONList(urlUserList) {
    const users = new XMLHttpRequest();
    users.open("GET", urlUserList, false);
    users.send(null);
    return users.responseText;
}

function loadProfile(userObject) {
    let firstName = document.getElementById("firstEdit");
    let lastName = document.getElementById("lastEdit");
    let bio = document.getElementById("bioEdit");
    let avatar = document.getElementById("avatar" + userObject.avatar);

    firstName.value = userObject.firstName;
    lastName.value = userObject.lastName;
    bio.innerText = userObject.bio;
    avatar.checked = true;
}

function saveProfile() {
    const firstName = document.getElementById("firstEdit").value;
    const lastName = document.getElementById("lastEdit").value;
    const bio = document.getElementById("bioEdit").value;
    const window = document.getElementById("ProfileEditor");
    const xhr = new XMLHttpRequest();
    const radios = document.getElementsByName("avatar");
    let avatar;

    for (let i = 0, length = radios.length; i < length; i++) {
        if (radios[i].checked) {
            avatar = radios[i].value;
            break;
        }
    }

    xhr.open("POST", url + "user/update", false);
    xhr.setRequestHeader('Content-Type', 'application/json');
    if (xhr.readyState === 1
        && firstName !== ""
        && lastName !== ""
        && bio !== ""
        && avatar !== "") {
        const updateUser = JSON.stringify({
                                              username: signedInUsername,
                                              firstName: firstName,
                                              lastName: lastName,
                                              bio: bio,
                                              avatar: avatar
                                          });
        xhr.send(updateUser);
    }
    window.classList.add('hidden');
    window.classList.remove('visible');
}

/**
 * Append all lists.
 */
function appendLists(username) {
    const userList = JSON.parse(getJSONList("rest/user/active"));
    const channelList = JSON.parse(getJSONList("rest/channel/all"));
    const userChannels = JSON.parse(getJSONList("rest/user/" + username + "/channels"));

    for (let i in userList) {
        if (userList[i].username === username && userList.hasOwnProperty(i)) {
            const userHeader = document.getElementById("SignedInName");
            userHeader.innerHTML += userList[i].firstName + " " + userList[i].lastName;
            loadProfile(userList[i]);
        } else if (userList.hasOwnProperty(i)) {
            const val = userList[i];
            const usernameTemp = val["username"];
            createList("user", val["firstName"] + " " + val["lastName"], usernameTemp);
        }
    }

    for (let i in userChannels) {
        if (userChannels.hasOwnProperty(i)) {
            let name = userChannels[i].name;
            let id = userChannels[i].id;
            createList("channel", name, id);
        }
    }

    for (let j in channelList) {
        let found = false;
        if (channelList.hasOwnProperty(j)) {
            for (let k in userChannels) {
                if (userChannels.hasOwnProperty(k)) {
                    let name1 = channelList[j].name;
                    let name2 = userChannels[k].name;
                    if (name1 === name2) {
                        found = true;
                        break;
                    }
                }
            }
        }
        if (found === false) {
            channels.push(channelList[j].name);
        }
    }
}

function createList(a, b, c) {
    let ul;
    const li = document.createElement("li");

    if (a === "channel") {
        ul = document.getElementById("ChannelList");
        li.appendChild(document.createTextNode("#" + b));
    } else {
        ul = document.getElementById("UserList");
        li.appendChild(document.createTextNode(b));
    }

    li.addEventListener('click', function () {
        debounceListener(200, msgInit(a, c));
    });
    li.setAttribute("id", c);
    li.setAttribute("class", "truncate");
    ul.appendChild(li);
}

/**************************************************************************************************
 * Creation of Messages
 **************************************************************************************************/

/**
 * Puts a message on the screen for the user, containing the sender, an avatar, the timestamp, and
 * the content.
 * @param message contains all the information to be displayed
 */
function createMessage(message) {
    if (message.content !== "") {
        const newMsg = document.createElement("div");
        newMsg.setAttribute("id", "msgContainer");
        const MsgInnerContainer = document.createElement("div");
        MsgInnerContainer.setAttribute("id", "MsgInnerContainer");
        const userName = document.createElement("div");

        userName.setAttribute("id", "name");
        userName.innerHTML += message.fromUsername;

        const timeStamp = document.createElement("div");
        timeStamp.setAttribute("id", "time");
        let timeStampShort = " ";

        // Prevents 'undefined' from showing up
        if (message.timestamp !== undefined) {

            // Define regexps to capture xx:xx and AM or PM
            var timeShortRE = new RegExp('(([0-9?][0-9+]*):([0-9?][0-9+])){1}');
            var ampmRE = new RegExp('AM|PM');

            // Derive values from message timestamp and glue together
            var timeShort = timeShortRE.exec(message.timestamp);
            var ampm = ampmRE.exec(message.timestamp);
            timeStampShort = timeShort[0] + " " + ampm;

        }
        timeStamp.innerHTML += timeStampShort;

        const avatar = document.createElement("div");
        avatar.setAttribute("id", "avatar");
        avatar.style.backgroundImage = "url(\"img/" + message.fromAvatar + ".jpg\")";

        const content = document.createElement("div");
        if (message.fromUsername === signedInUsername) {
            content.setAttribute("id", "msgContentRightAlign");
        } else {
            content.setAttribute("id", "msgContent");
        }
        content.innerHTML += message.content;

        if (message.fromUsername === signedInUsername) {
            MsgInnerContainer.append(userName, timeStamp, content);
            newMsg.style.textAlign = 'right';
            newMsg.append(MsgInnerContainer, avatar);
        } else {
            MsgInnerContainer.append(userName, timeStamp, content);
            newMsg.append(avatar, MsgInnerContainer);
        }

        log.append(newMsg);
        scrollBottom();
    }
}

/**
 * When the "send" button is clicked, this function executes.
 */
function send() {
    const field = document.getElementById("msg");
    const content = field.value;
    field.value = null;

    const json = JSON.stringify({
                                    "content": content,
                                    "toUsername": messageRecipientUsername
                                });
    ws.send(json);
}

function msgInit(a, b) {
    let messageList;

    clearMessagePanel();
    addActiveState(b);
    const host = document.location.host;
    const pathname = document.location.pathname;

    if (a === "channel") {
        wsConnect(new WebSocket("ws://" + host + pathname + "cm/" + b + "/" + signedInUsername));
        messageList = JSON.parse(
            getJSONList('rest/cm/from/' + b + '/all'));
    } else {
        wsConnect(new WebSocket(
            "ws://" + host + pathname + "dm/" + b + "/" + signedInUsername));
        messageList = JSON.parse(
            getJSONList('rest/dm/between/' + signedInUsername + '/' + b));
    }

    for (let i in messageList) {
        if (messageList.hasOwnProperty(i)) {
            const message = messageList[i];
            createMessage(message);
        }
    }

    if (a === "channel") {
        appendChannelSidebarUsers(b);
        showSidebar();
    } else {
        hideSidebar();
        messageRecipientUsername = b;
    }
    appendHeader();
    scrollBottom();
}

function appendHeader() {
    const selected = document.getElementsByClassName("selected");
    let content = selected[0].textContent;
    const chatHeader = document.getElementsByClassName("ChatHeader");
    chatHeader[0].textContent = content;
}

/**************************************************************************************************
 * Global Functions
 **************************************************************************************************/

function wsConnect(api) {
    if (ws.readyState === WebSocket.OPEN) {
        ws.close();
    }
    ws = api;
    ws.onmessage = function (event) {
        const message = JSON.parse(event.data);
        createMessage(message);
    };
}

/**
 * Add active state.
 * @param id id of the node given the selected state.
 */
function addActiveState(id) {
    const li = document.querySelectorAll("li");
    for (let n = 0; n < li.length; ++n) {
        if (li[n] !== this) {
            li[n].className = "";
        }
    }

    const active = document.getElementById(id);
    active.setAttribute("class", "selected");
}

function showElement(a) {
    let x = document.getElementById(a.id);
    x.classList.add('visible');
    x.classList.remove('hidden');
}

function hideElement(a) {
    let x = document.getElementById(a.id);
    x.classList.add('hidden');
    x.classList.remove('visible');
}

/**
 * Close search modal
 */
function closeModal() {
    const searchModal = document.getElementById("SearchOverlay");

    if (searchModal.classList.contains('visible')) {
        searchModal.classList.remove('visible');
        searchModal.classList.add('hidden');
    } else {
        searchModal.classList.add('visible');
        searchModal.classList.remove('hidden');
    }
}

/**
 * Scroll to bottom of messages.
 */
function scrollBottom() {
    const log = document.getElementById("log");
    log.scrollTop = log.scrollHeight;
}

/**
 * Clears all messages on the screen.
 */
function clearMessagePanel() {
    const log = document.getElementById("log");
    log.innerHTML = "";
}

/**************************************************************************************************
 * Event Listeners
 **************************************************************************************************/

/**
 * When a user hits enter and they typed their username and password into the main page form, then
 * the sign-in button is clicked.
 */
(function () {
    const message = document.getElementById("msg");
    const password = document.getElementById("password");

    message.addEventListener('keypress', function (event) {
        if (event.keyCode === 13) {
            event.preventDefault();
            document.getElementById("sendButton").click();
        }
    });

    password.addEventListener('keypress', function (event) {
        if (event.keyCode === 13) {
            event.preventDefault();
            document.getElementById("signInButton").click();
        }
    });
}());

function throttleListener(delay, fn) {
    let lastCall = 0;
    return function (...args) {
        const now = (new Date).getTime();
        if (now - lastCall < delay) {
            return;
        }
        lastCall = now;
        return fn(...args);
    }
}

function debounceListener(delay, fn) {
    let timerId;
    return function (...args) {
        if (timerId) {
            clearTimeout(timerId);
        }
        timerId = setTimeout(() => {
            fn(...args);
            timerId = null;
        }, delay);
    }
}

/**************************************************************************************************
 * Channel Functionality
 **************************************************************************************************/

/**
 * Joining channels functionality.
 */
function joinChannel() {
    const input = document.getElementById("typeahead");
    const content = input.value;
    input.value = null;
    const channelList = JSON.parse(getJSONList("rest/channel/all"));
    let channelID = null;
    let id = null;

    for (let i in channelList) {
        if (channelList.hasOwnProperty(i)) {
            const name = channelList[i].name;
            if (name === content) {
                id = channelList[i].id;
                channelID = id;
            }
        }
    }

    let x = new XMLHttpRequest();
    x.open("PUT", "rest/channel/" + channelID + "/user/" + signedInUsername, false);
    x.send();
    if (x.readyState === 4 && x.status === 200) {
        createList("channel", content, id);
        addActiveState(id);
        msgInit("channel", id);
        channels.splice(channels.indexOf(name), 1);
        closeModal();
    }
}

function appendChannelSidebarUsers(id) {
    let channelJSON = JSON.parse(getJSONList("rest/channel/" + id));
    let participants = channelJSON.participants;
    let moderators = channelJSON.mods;
    let ulParticipants = document.getElementById("participants");
    let ulModerators = document.getElementById("moderators");

    moderator = false;
    ulParticipants.innerHTML = '';
    ulModerators.innerHTML = '';

    checkMod(moderators);

    sidebarMap("mods", moderators, ulModerators, id);
    sidebarMap("users", participants, ulParticipants, id);
}

function checkMod(b) {
    for (let i in b) {
        if (b.hasOwnProperty(i)) {
            if (moderator === false) {
                moderator = b[i].username === signedInUsername;
                if (moderator === true) {
                    systemModerator = b[i].isMod;
                }
            }
        }
    }
}

function sidebarMap(a, b, c, d) {
    for (let i in b) {
        if (b.hasOwnProperty(i)) {
            let options = document.createElement("div");
            options.setAttribute("id", "options");

            let child = document.createElement("li");
            let span = document.createElement("span");
            span.setAttribute("class", "truncate");
            child.setAttribute("id", b[i].username);
            span.innerHTML += b[i].firstName + " " + b[i].lastName;
            child.appendChild(span);

            if (moderator === true
                && systemModerator === true
                && a === "mods"
                && b[i].username !== signedInUsername
                && b[i].isMod !== true) {
                let link3 = document.createElement("a");
                link3.addEventListener('click', function () {
                    debounceListener(200, demoteUser(b[i].username, d));
                });
                let demote = document.createElement("img");
                demote.setAttribute("src", "img/demote.svg");
                link3.appendChild(demote);

                options.appendChild(link3);
                child.appendChild(options);
            } else if (moderator === true && a === "users") {
                let link1 = document.createElement("a");
                link1.addEventListener('click', function () {
                    debounceListener(200, promoteUser(b[i].username, d));
                });
                let promote = document.createElement("img");
                promote.setAttribute("src", "img/promote.svg");
                link1.appendChild(promote);

                let link2 = document.createElement("a");
                link2.addEventListener('click', function () {
                    debounceListener(200, kickUser(b[i].username, d));
                });
                let kick = document.createElement("img");
                kick.setAttribute("src", "img/boot.svg");
                link2.appendChild(kick);

                options.appendChild(link1);
                options.appendChild(link2);
                child.appendChild(options);
            }
            c.appendChild(child);
        }
    }
}

function kickUser(user, channel) {
    let x = new XMLHttpRequest();
    x.open("DELETE", "rest/channel/" + channel + "/user/" + user, false);
    x.send();
    if (x.readyState === 4 && x.status === 200) {
        let parentNode = document.getElementById("participants");
        let nodes = parentNode.getElementsByTagName("li");
        for (let i in nodes) {
            if (nodes.hasOwnProperty(i)) {
                if (nodes[i].id === user) {
                    parentNode.removeChild(nodes[i]);
                    break;
                }
            }
        }
    }
}

function demoteUser(user, channel) {
    let x = new XMLHttpRequest();
    x.open("DELETE", "rest/channel/" + channel + "/mod/" + user, false);
    x.send();
    if (x.readyState === 4 && x.status === 200) {
        appendChannelSidebarUsers(channel);
    }
}

function promoteUser(user, channel) {
    let x = new XMLHttpRequest();
    x.open("PUT", "rest/channel/" + channel + "/mod/" + user, false);
    x.send();
    if (x.readyState === 4 && x.status === 200) {
        appendChannelSidebarUsers(channel);
    }
}

function showSidebar() {
    const sidebar = document.getElementById("channelSidebar");
    const log = document.getElementById("MessagesList");
    const sendMsg = document.getElementById("SendField");

    sidebar.classList.remove('hidden');
    sidebar.classList.add('visible');
    log.style.width = "60%";
    sidebar.style.width = "10%";
    sendMsg.style.width = "53vw"
}

function hideSidebar() {
    const sidebar = document.getElementById("channelSidebar");
    const log = document.getElementById("MessagesList");
    const sendMsg = document.getElementById("SendField");

    sidebar.classList.remove('visible');
    sidebar.classList.add('hidden');
    log.style.width = "75%";
    sidebar.style.width = "0";
    sendMsg.style.width = "68vw"
}

/**
 * Channel Search. Do NOT change anything here.
 */
const substringMatcher = function (strings) {
    return function findMatches(q, cb) {
        let matches = [];

        const substrRegex = new RegExp(q, 'i');

        $.each(strings, function (i, str) {
            if (substrRegex.test(str)) {
                matches.push(str);
            }
        });

        cb(matches);
    };
};

$('#ChannelSearchModal .typeahead')
    .typeahead({
                   hint: false,
                   highlight: true,
                   minLength: 1,
                   limit: 10
               },
               {
                   name: 'Channels',
                   source: substringMatcher(channels)
               });